import { Module } from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductController } from './product.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { Product } from 'src/entities/product.entity';
import { ProductRepository } from './product.repository';
import { TrackingProductRepository } from './trackingProduct.repository';
import { MongooseModule } from '@nestjs/mongoose';
import { ProductPrice, ProductPriceSchema } from 'src/schema/product.schema';
import { FirebaseService } from 'src/firebase/firebase.service';

@Module({
    imports: [
        TypeOrmModule.forFeature([TrackingProduct, Product]),
        MongooseModule.forFeature([{ name: ProductPrice.name, schema: ProductPriceSchema }]),
    ],
    controllers: [ProductController],
    providers: [ProductService, ProductRepository, TrackingProductRepository, FirebaseService],
})
export class ProductModule {}
