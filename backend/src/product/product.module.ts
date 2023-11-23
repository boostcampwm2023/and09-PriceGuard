import { Module } from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductController } from './product.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { Product } from 'src/entities/product.entity';
import { ProductRepository } from './product.repository';
import { TrackingProductRepository } from './trackingProduct.repository';

@Module({
    imports: [TypeOrmModule.forFeature([TrackingProduct, Product])],
    controllers: [ProductController],
    providers: [ProductService, ProductRepository, TrackingProductRepository],
})
export class ProductModule {}
