import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { Repository } from 'typeorm';
import { TrackingProductDto } from 'src/dto/product.tracking.dto';

@Injectable()
export class ProductService {
    constructor(
        @InjectRepository(TrackingProduct)
        private trackingProductRepository: Repository<TrackingProduct>,
    ) {}
    verifyUrl(productUrlDto: ProductUrlDto) {
        console.log(productUrlDto);
    }

    addProduct(productDto: ProductDto) {
        console.log(productDto);
    }

    async getTrackingList(userId: string): Promise<TrackingProductDto[]> {
        const trackingProductList = await this.trackingProductRepository.find({
            where: { userId: userId },
            relations: ['product'],
        });
        if (trackingProductList.length === 0) {
            throw new HttpException('상품 목록을 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        const trackingListInfo = trackingProductList.map((product) => {
            const { productName, productCode, shop, shopUrl, imageUrl } = product.product;
            return {
                productName,
                productCode,
                shop,
                shopUrl,
                imageUrl,
                targetPrice: product.targetPrice,
                price: 1234, // 임시 더미 가격 데이터
            };
        });

        return trackingListInfo;
    }

    getRecommendList() {}

    getProductDetails(productCode: string) {
        console.log(productCode);
    }

    updateTargetPrice(productDto: ProductDto) {
        console.log(productDto);
    }

    deleteProduct(productCode: string) {
        console.log(productCode);
    }
}
