import { Injectable } from '@nestjs/common';
import { ProductRankCache } from './rank.cache';
import { MAX_TRACKING_PRODUCT_CACHE, MAX_TRACKING_RANK } from 'src/constants';
import { TrackingProductCache } from './tracking.cache';
import { InjectRepository } from '@nestjs/typeorm';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';
import { ProductPrice } from 'src/schema/product.schema';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { ProductRepository } from 'src/product/product.repository';
import { TrackingProductRepository } from 'src/product/trackingProduct.repository';
import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';

@Injectable()
export class CacheService {
    private productRankCache = new ProductRankCache(MAX_TRACKING_RANK);
    private trackingProductCache = new TrackingProductCache(MAX_TRACKING_PRODUCT_CACHE);
    constructor(
        @InjectRepository(TrackingProductRepository)
        private trackingProductRepository: TrackingProductRepository,
        @InjectRepository(ProductRepository)
        private productRepository: ProductRepository,
        @InjectModel(ProductPrice.name)
        private productPriceModel: Model<ProductPrice>,
        @InjectRedis() private readonly redis: Redis,
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
        const rankList = await this.productRepository.getTotalInfoRankingList();
        const initPromise = latestData.map(async (data) => {
            const matchProduct = userCountList.find((product) => product.id === data._id);
            const setUserCount = await this.redis.set(
                `product:${data._id}`,
                JSON.stringify({
                    isSoldOut: data.isSoldOut,
                    price: data.price,
                    lowestPrice: data.lowestPrice,
                }),
            );
            const zaddUserCount = await this.redis.zadd(
                'userCount',
                matchProduct ? parseInt(matchProduct.userCount) : 0,
                data._id,
            );
            return Promise.all([setUserCount, zaddUserCount]);
        });
        rankList.forEach((product) => {
            this.productRankCache.put(product.id, { ...product, userCount: parseInt(product.userCount) });
        });
        await Promise.all(initPromise);
    }

    putProductRank(key: string, value: ProductRankCacheDto) {
        this.productRankCache.put(key, value);
    }

    updateProductRank(product: ProductRankCacheDto, newProduct?: ProductRankCacheDto) {
        this.productRankCache.update(product, newProduct);
    }

    findIndexProductRank(key: string) {
        return this.productRankCache.findIndex(key);
    }

    getProductRank(key: string) {
        return this.productRankCache.get(key);
    }
    getAllProductRank() {
        return this.productRankCache.getAll();
    }

    putTrackingProduct(key: string, value: TrackingProduct[]) {
        this.trackingProductCache.put(key, value);
    }

    getTrackingProduct(key: string) {
        return this.trackingProductCache.get(key);
    }

    getAllTrackingProduct() {
        return this.trackingProductCache.getAll();
    }

    addValueTrackingProduct(key: string, value: TrackingProduct) {
        this.trackingProductCache.addValue(key, value);
    }

    deleteValueTrackingProdcut(key: string, value: TrackingProduct) {
        this.trackingProductCache.deleteValue(key, value);
    }

    updateValueTrackingProdcut(key: string, value: TrackingProduct) {
        this.trackingProductCache.updateValue(key, value);
    }
}
