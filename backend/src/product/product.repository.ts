import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Product } from 'src/entities/product.entity';
import { createUrl11st } from 'src/utils/openapi.11st';
import { ProductDto } from 'src/dto/product.dto';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { MAX_TRACKING_RANK } from 'src/constants';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';

@Injectable()
export class ProductRepository extends Repository<Product> {
    constructor(
        @InjectRepository(Product)
        private repository: Repository<Product>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveProduct(productDto: ProductDto | ProductInfoDto): Promise<Product> {
        const { productName, productCode, shop, imageUrl } = productDto;
        const shopUrl = createUrl11st(productCode);
        const newProduct = Product.create({ productName, productCode, shop, shopUrl, imageUrl });
        await newProduct.save();
        return newProduct;
    }

    async getTotalInfoRankingList() {
        const recommendList = await this.repository
            .createQueryBuilder('product')
            .select([
                'product.id as id',
                'COUNT(tracking_product.userId) as userCount',
                'product.productName as productName',
                'product.productCode as productCode',
                'product.shop as shop',
                'product.imageUrl as imageUrl',
            ])
            .leftJoin(TrackingProduct, 'tracking_product', 'tracking_product.productId = product.id')
            .groupBy('product.id')
            .orderBy('userCount', 'DESC')
            .addOrderBy('MAX(product.id)', 'DESC')
            .limit(MAX_TRACKING_RANK)
            .getRawMany();
        return recommendList;
    }
}
