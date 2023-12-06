import { HttpException, HttpStatus, Inject, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductAddDto } from '../dto/product.add.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProductDto } from 'src/dto/product.tracking.dto';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { TrackingProductRepository } from './trackingProduct.repository';
import { ProductRepository } from './product.repository';
import { getProductInfo11st } from 'src/utils/openapi.11st';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import { InjectModel } from '@nestjs/mongoose';
import { ProductPrice } from 'src/schema/product.schema';
import { Model } from 'mongoose';
import { ProductPriceDto } from 'src/dto/product.price.dto';
import { PriceDataDto } from 'src/dto/price.data.dto';
import { MAX_TRACKING_RANK, NINETY_DAYS, NO_CACHE, THIRTY_DAYS } from 'src/constants';
import { Cron } from '@nestjs/schedule';
import { ProductRankCache } from 'src/utils/cache';
import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';
import { CACHE_MANAGER } from '@nestjs/cache-manager';
import { Cache } from 'cache-manager';
import { FirebaseService } from '../firebase/firebase.service';
import { Message } from 'firebase-admin/lib/messaging/messaging-api';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { FirebaseRepository } from '../firebase/firebase.repository';

const REGEXP_11ST =
    /http[s]?:\/\/(?:www\.|m\.)?11st\.co\.kr\/products\/(?:ma\/|m\/|pa\/)?([1-9]\d*)(?:\?.*)?(?:\/share)?/;
@Injectable()
export class ProductService {
    private productDataCache = new Map(); //Redis 대체 예정
    private productRankCache = new ProductRankCache(MAX_TRACKING_RANK);
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectRepository(FirebaseRepository)
        private firebaseRepository: FirebaseRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
        @Inject(CACHE_MANAGER) private cacheManager: Cache,
        private readonly firebaseService: FirebaseService,
    ) {
        this.initCache();
    }

    async initCache() {
        const latestData = await this.productPriceModel
            .aggregate([
                {
                    $sort: { time: -1 },
                },
                {
                    $group: {
                        _id: '$productId',
                        price: { $first: '$price' },
                        isSoldOut: { $first: '$isSoldOut' },
                        lowestPrice: { $min: '$price' },
                    },
                },
            ])
            .sort('_id')
            .exec();
        const userCountList = await this.trackingProductRepository.getAllUserCount();
        const rankList = await this.trackingProductRepository.getTotalInfoRankingList();
        latestData.forEach((data, idx) => {
            const matchProduct = userCountList.find((product) => product.id === data._id);
            this.productDataCache.set(data._id, {
                price: data.price,
                isSoldOut: data.isSoldOut,
                lowestPrice: data.lowestPrice,
                userCount: matchProduct ? parseInt(matchProduct.userCount) : 0,
                updateAt: Date.now() + idx,
            });
        });
        rankList.forEach((product) => {
            this.productRankCache.put(product.id, { ...product, userCount: parseInt(product.userCount) });
        });
    }

    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductInfoDto> {
        const { productUrl } = productUrlDto;
        const matchList = productUrl.match(REGEXP_11ST);
        if (!matchList) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
        const productCode = matchList[1];
        return await getProductInfo11st(productCode);
    }

    async addProduct(userId: string, productAddDto: ProductAddDto) {
        const { productCode, targetPrice } = productAddDto;
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
        });
        const product = existProduct ?? (await this.firstAddProduct(productCode));
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { productId: product.id, userId: userId },
        });
        if (trackingProduct) {
            throw new HttpException('이미 등록된 상품입니다.', HttpStatus.CONFLICT);
        }
        const cacheData = await this.productDataCache.get(product.id);
        const productRank = {
            id: product.id,
            productName: product.productName,
            productCode: product.productCode,
            shop: product.shop,
            imageUrl: product.imageUrl,
            userCount: cacheData.userCount + 1,
        };
        this.productRankCache.update(productRank);
        this.productDataCache.set(product.id, {
            ...cacheData,
            userCount: cacheData.userCount + 1,
            updateAt: Date.now(),
        });
        await this.trackingProductRepository.saveTrackingProduct(userId, product.id, targetPrice);
    }

    async getTrackingList(userId: string): Promise<TrackingProductDto[]> {
        const trackingProductList = await this.trackingProductRepository.find({
            where: { userId: userId },
            relations: ['product'],
        });
        if (trackingProductList.length === 0) return [];
        const trackingListInfo = trackingProductList.map(async ({ product, targetPrice, isAlert }) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const { price } = this.productDataCache.get(id) ?? { price: NO_CACHE };
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            return {
                productName,
                productCode,
                shop,
                imageUrl,
                targetPrice: targetPrice,
                price,
                isAlert,
                priceData,
            };
        });
        const result = await Promise.all(trackingListInfo);
        return result;
    }

    async getRecommendList() {
        const recommendList = this.productRankCache.getAll();
        const recommendListInfo = recommendList.map(async (product, index) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const { price } = this.productDataCache.get(id) ?? { price: NO_CACHE };
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            return {
                productName,
                productCode,
                shop,
                imageUrl,
                price,
                rank: index + 1,
                priceData,
            };
        });
        const result = await Promise.all(recommendListInfo);
        return result;
    }

    async getProductDetails(userId: string, productCode: string): Promise<ProductDetailsDto> {
        const selectProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
        });
        if (!selectProduct) {
            throw new HttpException('상품 정보가 존재하지 않습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { userId: userId, productId: selectProduct.id },
        });
        const ranklist = await this.trackingProductRepository.getRankingList();
        const idx = ranklist.findIndex(({ id }) => id === selectProduct.id);
        const rank = idx === -1 ? idx : idx + 1;
        const priceData = await this.getPriceData(selectProduct.id, NINETY_DAYS);
        const { price, lowestPrice } = this.productDataCache.get(selectProduct.id);
        return {
            productName: selectProduct.productName,
            shop: selectProduct.shop,
            imageUrl: selectProduct.imageUrl,
            rank: rank,
            shopUrl: selectProduct.shopUrl,
            targetPrice: trackingProduct ? trackingProduct.targetPrice : -1,
            lowestPrice: lowestPrice,
            price: price,
            priceData: priceData,
        };
    }

    async updateTargetPrice(userId: string, productAddDto: ProductAddDto) {
        const product = await this.findTrackingProductByCode(userId, productAddDto.productCode);
        product.targetPrice = productAddDto.targetPrice;
        product.isFirst = true;
        await this.trackingProductRepository.save(product);
    }

    async deleteProduct(userId: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, productCode);
        const cacheData = await this.productDataCache.get(product.productId);
        const prevProduct = this.productRankCache.get(product.productId)?.value;
        this.productDataCache.set(product.productId, {
            ...cacheData,
            userCount: cacheData.userCount - 1,
            updateAt: Date.now(),
        });
        if (!prevProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        prevProduct.userCount--;
        if (this.productDataCache.size > MAX_TRACKING_RANK) {
            await this.deleteUpdateCache(prevProduct);
        } else {
            this.productRankCache.update(prevProduct);
        }
        await this.trackingProductRepository.remove(product);
    }

    async deleteUpdateCache(prevProduct: ProductRankCacheDto) {
        //Redis 연산으로 대체 예정
        const rankList = [...this.productDataCache.entries()].sort((a, b) => {
            const [, infoA] = a;
            const [, infoB] = b;
            if (infoB.userCount === infoA.userCount) {
                return infoB.updateAt - infoA.updateAt;
            }
            return infoB.userCount - infoB.userCount;
        });
        const [nextDataId, nextDataInfo] = rankList[MAX_TRACKING_RANK];

        if (nextDataInfo.userCount > prevProduct.userCount) {
            const newProduct = await this.productRepository.findOne({
                where: { id: nextDataId },
            });
            if (!newProduct) {
                throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
            }
            const newProductRanck = {
                id: newProduct.id,
                productName: newProduct.productName,
                productCode: newProduct.productCode,
                shop: newProduct.shop,
                imageUrl: newProduct.imageUrl,
                userCount: nextDataInfo.userCount,
            };
            this.productRankCache.update(prevProduct, newProductRanck);
        }
    }

    async findTrackingProductByCode(userId: string, productCode: string) {
        const existProduct = await this.productRepository.findOne({
            where: { productCode: productCode },
        });
        if (!existProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingProduct = await this.trackingProductRepository.findOne({
            where: { userId: userId, productId: existProduct.id },
        });
        if (!trackingProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        return trackingProduct;
    }

    async mongo(productPriceDto: ProductPriceDto) {
        const newData = new this.productPriceModel(productPriceDto);
        return newData.save();
    }

    async getPriceData(productId: string, days: number): Promise<PriceDataDto[]> {
        const endDate = new Date();
        const startDate = new Date(endDate);
        startDate.setDate(endDate.getDate() - days);
        const dataInfo = await this.productPriceModel
            .find({
                productId: productId,
                time: {
                    $gte: startDate,
                    $lte: endDate,
                },
            })
            .exec();
        return dataInfo.map(({ time, price, isSoldOut }) => {
            return { time: new Date(time).getTime(), price, isSoldOut };
        });
    }

    @Cron('* */10 * * * *')
    async cyclicPriceChecker() {
        const productList = await this.productRepository.find({ select: { id: true, productCode: true } });
        const productCodeList = productList.map(({ productCode, id }) => getProductInfo11st(productCode, id));
        const results = await Promise.all(productCodeList);
        const updatedDataInfo = results.filter(({ productId, productPrice, isSoldOut }) => {
            const cache = this.productDataCache.get(productId);
            if (!cache || cache.isSoldOut !== isSoldOut || cache.price !== productPrice) {
                const lowestPrice = cache ? Math.min(cache.lowestPrice, productPrice) : productPrice;
                this.productDataCache.set(productId, {
                    isSoldOut,
                    price: productPrice,
                    lowestPrice,
                });
                return true;
            }
            return false;
        });
        if (updatedDataInfo.length > 0) {
            await this.productPriceModel.insertMany(
                updatedDataInfo.map(({ productId, productPrice, isSoldOut }) => {
                    return { productId, price: productPrice, isSoldOut };
                }),
            );
            const { messages, products } = await this.getNotifications(updatedDataInfo);
            if (messages.length > 0) {
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
        }
    }
    getMessage(productName: string, productPrice: number, imageUrl: string, token: string): Message {
        return {
            notification: {
                title: '목표 가격 이하로 내려갔습니다!',
                body: `${productName}의 현재 가격은 ${productPrice}원 입니다.`,
            },
            android: {
                notification: {
                    imageUrl,
                },
            },
            token,
        };
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
            productInfo.map(async ({ productId, productName, productPrice, imageUrl }) => {
                const trackingList = productId ? trackingMap.get(productId) || [] : [];
                const notifications = [];
                const matchedProducts = [];

                for (const trackingProduct of trackingList) {
                    const { userId, targetPrice, isFirst, isAlert } = trackingProduct;
                    if (!isFirst && targetPrice < productPrice) {
                        trackingProduct.isFirst = true;
                        await this.trackingProductRepository.save(trackingProduct);
                    } else if (targetPrice >= productPrice && isFirst && isAlert) {
                        const deviceInfo = await this.firebaseRepository.findOne({ where: { userId: userId } });
                        if (deviceInfo) {
                            notifications.push(this.getMessage(productName, productPrice, imageUrl, deviceInfo.token));
                            matchedProducts.push(trackingProduct);
                        }
                    }
                }

                return { notifications, matchedProducts };
            }),
        );

        const allNotifications = results.flatMap((result) => result.notifications);
        const allMatchedProducts = results.flatMap((result) => result.matchedProducts);

        return {
            messages: allNotifications,
            products: allMatchedProducts,
        };
    }
    async firstAddProduct(productCode: string) {
        const productInfo = await getProductInfo11st(productCode);
        const product = await this.productRepository.saveProduct(productInfo);
        const updatedDataInfo = {
            productId: product.id,
            price: productInfo.productPrice,
            isSoldOut: productInfo.isSoldOut,
        };

        this.productDataCache.set(product.id, {
            price: productInfo.productPrice,
            isSoldOut: productInfo.isSoldOut,
            lowestPrice: productInfo.productPrice,
            userCount: 0,
        });
        this.productPriceModel.create(updatedDataInfo);
        return product;
    }

    async toggleProductAlert(userId: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, productCode);
        product.isAlert = !product.isAlert;
        await this.trackingProductRepository.save(product);
    }
}
