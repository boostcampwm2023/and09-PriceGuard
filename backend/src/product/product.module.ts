import { Module } from '@nestjs/common';
import { ProductService } from './product.service';
import { ProductController } from './product.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';

@Module({
    imports: [TypeOrmModule.forFeature([TrackingProduct])],
    controllers: [ProductController],
    providers: [ProductService],
})
export class ProductModule {}
