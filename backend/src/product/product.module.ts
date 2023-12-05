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
import { CacheModule } from '@nestjs/cache-manager';
import { REDIS_HOST, REDIS_PASSWORD, REDIS_PORT } from 'src/constants';
import * as redisStore from 'cache-manager-redis-store';
import { FirebaseService } from 'src/firebase/firebase.service';
import { FirebaseRepository } from 'src/firebase/firebase.repository';
import { FirebaseToken } from 'src/entities/firebase.token.entity';

@Module({
    imports: [
        TypeOrmModule.forFeature([TrackingProduct, Product, FirebaseToken]),
        MongooseModule.forFeature([{ name: ProductPrice.name, schema: ProductPriceSchema }]),
        CacheModule.register({
            store: redisStore,
            host: REDIS_HOST,
            port: REDIS_PORT,
            ttl: 0,
            password: REDIS_PASSWORD,
        }),
    ],
    controllers: [ProductController],
    providers: [ProductService, ProductRepository, TrackingProductRepository, FirebaseService, FirebaseRepository],
})
export class ProductModule {}
