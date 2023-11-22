import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Product } from 'src/entities/product.entity';
import { ProductDetailsDto } from '../dto/product.details.dto';
import { createUrl11st } from 'src/utils/openapi.11st';

@Injectable()
export class ProductRepository extends Repository<Product> {
    constructor(
        @InjectRepository(Product)
        private repository: Repository<Product>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveProduct(productDetailsDto: ProductDetailsDto): Promise<Product> {
        const { productName, productCode, shop, imageUrl } = productDetailsDto;
        const shopUrl = createUrl11st(productCode);
        const newProduct = Product.create({ productName, productCode, shop, shopUrl, imageUrl });
        await newProduct.save();
        return newProduct;
    }
}
