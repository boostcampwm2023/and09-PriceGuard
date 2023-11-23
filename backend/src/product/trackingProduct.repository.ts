import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';

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

    async getRankigList() {
        const recommendList = await this.repository
            .createQueryBuilder('tracking_product')
            .select('tracking_product.productId as productId')
            .groupBy('tracking_product.productId')
            .orderBy('COUNT(tracking_product.userId)', 'DESC')
            .getRawMany();
        return recommendList;
    }
}
