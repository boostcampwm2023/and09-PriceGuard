import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
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
import { PriceDataDto } from 'src/dto/price.data.dto';
import { MAX_TRACKING_RANK, NINETY_DAYS, THIRTY_DAYS, TWENTY_MIN_TO_SEC } from 'src/constants';
import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';
import { CacheService } from 'src/cache/cache.service';

const REGEXP_11ST =
    /http[s]?:\/\/(?:www\.|m\.)?11st\.co\.kr\/products\/(?:ma\/|m\/|pa\/)?([1-9]\d*)(?:\?.*)?(?:\/share)?/;
@Injectable()
export class ProductService {
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
        @InjectRedis() private readonly redis: Redis,
        private cacheService: CacheService,
    ) {}

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
        const userCount = parseInt(await this.redis.zincrby('userCount', 1, product.id));
        const newProductRank = {
            id: product.id,
            productName: product.productName,
            productCode: product.productCode,
            shop: product.shop,
            imageUrl: product.imageUrl,
            userCount: userCount,
        };
        const newTrackingProduct = await this.trackingProductRepository.saveTrackingProduct(
            userId,
            product.id,
            targetPrice,
        );
        const trackingProductList = this.cacheService.getTrackingProduct(userId);
        if (trackingProductList) {
            newTrackingProduct.product = product;
            this.cacheService.addValueTrackingProduct(userId, newTrackingProduct);
        }
        this.cacheService.updateProductRank(newProductRank);
    }

    async getTrackingList(userId: string): Promise<TrackingProductDto[]> {
        let trackingProductList = this.cacheService.getTrackingProduct(userId);
        if (!trackingProductList) {
            trackingProductList = await this.trackingProductRepository.find({
                where: { userId: userId },
                relations: ['product'],
            });
            this.cacheService.putTrackingProduct(userId, trackingProductList);
        }
        if (trackingProductList.length === 0) return [];
        const trackingListInfo = trackingProductList.map(async ({ product, targetPrice, isAlert }) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            const { price } = priceData[priceData.length - 1];
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
        const recommendList = this.cacheService.getAllProductRank();
        const recommendListInfo = recommendList.map(async (product, index) => {
            const { id, productName, productCode, shop, imageUrl } = product;
            const priceData = await this.getPriceData(id, THIRTY_DAYS);
            const { price } = priceData[priceData.length - 1];
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
        await this.trackingProductRepository.getUserCount(selectProduct.id);
        const idx = this.cacheService.findIndexProductRank(selectProduct.id);
        const rank = idx === -1 ? idx : idx + 1;
        const priceData = await this.getPriceData(selectProduct.id, NINETY_DAYS);
        const { price } = priceData[priceData.length - 1];
        const lowestPrice = Math.min(...priceData.map((item) => item.price));
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
        const currentProduct = this.cacheService.getProductRank(product.productId);
        await this.redis.zincrby('userCount', -1, product.productId);
        if (currentProduct) {
            currentProduct.userCount--;
            const productCount = await this.redis.zcard('userCount');
            if (productCount > MAX_TRACKING_RANK) {
                await this.deleteUpdateCache(currentProduct);
            } else {
                this.cacheService.updateProductRank(currentProduct);
            }
        }
        if (product) {
            this.cacheService.deleteValueTrackingProdcut(userId, product);
        }
        await this.trackingProductRepository.remove(product);
    }

    async deleteUpdateCache(currentProduct: ProductRankCacheDto) {
        const nextProductData = await this.redis.zrevrange(
            'userCount',
            MAX_TRACKING_RANK,
            MAX_TRACKING_RANK,
            'WITHSCORES',
        );
        const [nextDataId, userCount] = [nextProductData[0], parseInt(nextProductData[1])];
        if (userCount < currentProduct.userCount) {
            this.cacheService.updateProductRank(currentProduct);
            return;
        }
        if (userCount === currentProduct.userCount && nextDataId < currentProduct.id) {
            this.cacheService.updateProductRank(currentProduct);
            return;
        }
        const newProduct = await this.productRepository.findOne({
            where: { id: nextDataId },
        });
        if (!newProduct) {
            throw new HttpException('상품을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const newProductRank = {
            id: newProduct.id,
            productName: newProduct.productName,
            productCode: newProduct.productCode,
            shop: newProduct.shop,
            imageUrl: newProduct.imageUrl,
            userCount: userCount,
        };
        this.cacheService.updateProductRank(currentProduct, newProductRank);
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
        trackingProduct.product = existProduct;
        return trackingProduct;
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

    async firstAddProduct(productCode: string) {
        const productInfo = await getProductInfo11st(productCode);
        const product = await this.productRepository.saveProduct(productInfo);
        const updatedDataInfo = {
            productId: product.id,
            price: productInfo.productPrice,
            isSoldOut: productInfo.isSoldOut,
        };
        this.redis.set(
            `product:${product.id}`,
            JSON.stringify({
                price: productInfo.productPrice,
                isSoldOut: productInfo.isSoldOut,
                lowestPrice: productInfo.productPrice,
            }),
            'EX',
            TWENTY_MIN_TO_SEC,
        );
        this.productPriceModel.create(updatedDataInfo);
        return product;
    }

    async toggleProductAlert(userId: string, productCode: string) {
        const product = await this.findTrackingProductByCode(userId, productCode);
        product.isAlert = !product.isAlert;
        await this.trackingProductRepository.save(product);
        this.cacheService.updateValueTrackingProdcut(userId, product);
    }

    async getProductCurrentData(productId: string) {
        const cacheData = await this.redis.get(`product:${productId}`);
        if (cacheData) {
            const { price, lowestPrice } = JSON.parse(cacheData);
            return { price, lowestPrice };
        }
        const latestData = await this.productPriceModel
            .aggregate([
                {
                    $match: { productId: productId },
                },
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
            .exec();
        const { price, lowestPrice } = latestData[0];
        return { price, lowestPrice };
    }
}
