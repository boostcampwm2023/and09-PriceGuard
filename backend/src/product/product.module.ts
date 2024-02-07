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
import { CronService } from 'src/cron/cron.service';
import { CacheService } from 'src/cache/cache.service';
import { UsersRepository } from 'src/user/user.repository';
import { User } from 'src/entities/user.entity';

@Module({
    imports: [
        TypeOrmModule.forFeature([TrackingProduct, Product, User]),
        MongooseModule.forFeature([{ name: ProductPrice.name, schema: ProductPriceSchema }]),
    ],
    controllers: [ProductController],
    providers: [
        ProductService,
        ProductRepository,
        TrackingProductRepository,
        UsersRepository,
        FirebaseService,
        CronService,
        CacheService,
    ],
    exports: [CacheService],
})
export class ProductModule {}
