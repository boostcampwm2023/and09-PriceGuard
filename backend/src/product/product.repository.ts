import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Product } from 'src/entities/product.entity';
import { createUrl11st } from 'src/utils/openapi.11st';
import { ProductDto } from 'src/dto/product.dto';
import { ProductDetailsDto } from 'src/dto/product.details.dto';

@Injectable()
export class ProductRepository extends Repository<Product> {
    constructor(
        @InjectRepository(Product)
        private repository: Repository<Product>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }

    async saveProduct(productDto: ProductDto | ProductDetailsDto): Promise<Product> {
        const { productName, productCode, shop, imageUrl } = productDto;
        const shopUrl = createUrl11st(productCode);
        const newProduct = Product.create({ productName, productCode, shop, shopUrl, imageUrl });
        await newProduct.save();
        return newProduct;
    }
}
