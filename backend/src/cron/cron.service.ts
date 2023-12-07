import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { TrackingProductRepository } from '../product/trackingProduct.repository';
import { ProductRepository } from '../product/product.repository';
import { getProductInfo11st } from 'src/utils/openapi.11st';
import { InjectModel } from '@nestjs/mongoose';
import { ProductPrice } from 'src/schema/product.schema';
import { Model } from 'mongoose';
import { CHANNEL_ID } from 'src/constants';
import { Cron } from '@nestjs/schedule';
import { FirebaseService } from '../firebase/firebase.service';
import { Message } from 'firebase-admin/lib/messaging/messaging-api';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';

@Injectable()
export class CronService {
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
        @InjectRedis() private readonly redis: Redis,
        private readonly firebaseService: FirebaseService,
    ) {}

    private isDefined = <T>(x: T | undefined): x is T => x !== undefined;

    @Cron('* */10 * * * *')
    async cyclicPriceChecker() {
        const totalProducts = await this.productRepository.find({ select: { id: true, productCode: true } });
        const recentProductInfo = await Promise.all(
            totalProducts.map(({ productCode, id }) => getProductInfo11st(productCode, id)),
        );
        const checkProducts = await Promise.all(recentProductInfo.map((data) => this.getUpdatedProduct(data)));
        const updatedProducts = checkProducts.filter(this.isDefined);
        if (updatedProducts.length > 0) {
            await this.productPriceModel.insertMany(
                updatedProducts.map(({ productId, productPrice, isSoldOut }) => {
                    return { productId, price: productPrice, isSoldOut };
                }),
            );
            await this.pushNotifications(updatedProducts);
        }
    }

    async getUpdatedProduct(data: ProductInfoDto) {
        const { productId, productPrice, isSoldOut } = data;
        const cacheData = await this.redis.get(`product:${productId}`);
        const cache = JSON.parse(cacheData as string);
        if (!cache || cache.isSoldOut !== isSoldOut || cache.price !== productPrice) {
            const lowestPrice = cache ? Math.min(cache.lowestPrice, productPrice) : productPrice;
            await this.redis.set(
                `product:${productId}`,
                JSON.stringify({
                    isSoldOut,
                    price: productPrice,
                    lowestPrice,
                }),
            );
            return data;
        }
    }

    async pushNotifications(updatedProducts: ProductInfoDto[]) {
        const { messages, products } = await this.getNotifications(updatedProducts);
        if (messages.length === 0) return;
        const { responses } = await this.firebaseService.getMessaging().sendEach(messages);
        const successProducts = products.filter((item, index) => {
            const { success } = responses[index];
            item.isFirst = false;
            return success;
        });
        if (successProducts.length > 0) {
            await this.trackingProductRepository.save(successProducts);
        }
    }
    async getNotifications(
        productInfo: ProductInfoDto[],
    ): Promise<{ messages: Message[]; products: TrackingProduct[] }> {
        const productIds = productInfo.map((p) => p.productId);

        const trackingProducts = await this.trackingProductRepository
            .createQueryBuilder('tracking_product')
            .where('tracking_product.productId IN (:...productIds)', { productIds })
            .getMany();

        const trackingMap = new Map<string, TrackingProduct[]>();
        trackingProducts.forEach((tracking) => {
            const products = trackingMap.get(tracking.productId) || [];
            trackingMap.set(tracking.productId, [...products, tracking]);
        });
        const results = await Promise.all(
            productInfo.map(async (product) => {
                const trackingList = product.productId ? trackingMap.get(product.productId) || [] : [];
                return await this.findMatchedProducts(trackingList, product);
            }),
        );

        const allNotifications = results.flatMap((result) => result.notifications);
        const allMatchedProducts = results.flatMap((result) => result.matchedProducts);

        return {
            messages: allNotifications,
            products: allMatchedProducts,
        };
    }

    async findMatchedProducts(trackingList: TrackingProduct[], product: ProductInfoDto) {
        const { productPrice, productCode, productName, imageUrl } = product;
        const notifications = [];
        const matchedProducts = [];

        for (const trackingProduct of trackingList) {
            const { userId, targetPrice, isFirst, isAlert } = trackingProduct;
            if (!isFirst && targetPrice < productPrice) {
                trackingProduct.isFirst = true;
                await this.trackingProductRepository.save(trackingProduct);
            } else if (targetPrice >= productPrice && isFirst && isAlert) {
                const firebaseToken = await this.redis.get(`firebaseToken:${userId}`);
                if (firebaseToken) {
                    notifications.push(
                        this.getMessage(productCode, productName, productPrice, imageUrl, firebaseToken),
                    );
                    matchedProducts.push(trackingProduct);
                }
            }
        }
        return { notifications, matchedProducts };
    }

    private getMessage(
        productCode: string,
        productName: string,
        productPrice: number,
        imageUrl: string,
        token: string,
    ): Message {
        return {
            notification: {
                title: '목표 가격 이하로 내려갔습니다!',
                body: `${productName}의 현재 가격은 ${productPrice}원 입니다.`,
            },
            data: {
                productCode,
            },
            android: {
                notification: {
                    channelId: CHANNEL_ID,
                    imageUrl,
                },
            },
            token,
        };
    }
}
