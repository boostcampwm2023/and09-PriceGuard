import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { Product } from 'src/entities/product.entity';
import { MAX_TRACKING_RANK } from 'src/constants';

@Injectable()
export class TrackingProductRepository extends Repository<TrackingProduct> {
    constructor(
        @InjectRepository(TrackingProduct)
        private repository: Repository<TrackingProduct>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveTrackingProduct(userId: string, productId: string, targetPrice: number): Promise<TrackingProduct> {
        const newTrackingProduct = TrackingProduct.create({ userId, productId, targetPrice });
        await newTrackingProduct.save();
        return newTrackingProduct;
    }

    async getTotalInfoRankingList() {
        const recommendList = await this.repository
            .createQueryBuilder('tracking_product')
            .select([
                'tracking_product.productId as id',
                'COUNT(tracking_product.userId) as userCount',
                'product.productName as productName',
                'product.productCode as productCode',
                'product.shop as shop',
                'product.imageUrl as imageUrl',
            ])
            .leftJoin(Product, 'product', 'tracking_product.productId = product.id')
            .groupBy('tracking_product.productId')
            .orderBy('userCount', 'DESC')
            .addOrderBy('MAX(tracking_product.productId)', 'DESC')
            .limit(MAX_TRACKING_RANK)
            .getRawMany();
        return recommendList;
    }

    async getRankingList() {
        const rankList = await this.repository
            .createQueryBuilder('tracking_product')
            .select('tracking_product.productId as id')
            .groupBy('tracking_product.productId')
            .orderBy('COUNT(tracking_product.userId)', 'DESC')
            .addOrderBy('MAX(tracking_product.productId)', 'DESC')
            .limit(MAX_TRACKING_RANK)
            .getRawMany();
        return rankList;
    }

    async getAllUserCount() {
        const raw = await this.repository
            .createQueryBuilder('tracking_product')
            .select(['tracking_product.productId as id', 'COUNT(tracking_product.userId) as userCount'])
            .groupBy('tracking_product.productId')
            .getRawMany();
        return raw;
    }
}
