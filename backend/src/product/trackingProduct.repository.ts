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
}
