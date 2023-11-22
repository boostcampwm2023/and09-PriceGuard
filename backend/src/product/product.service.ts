import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { Repository } from 'typeorm';
import { TrackingProductDto } from 'src/dto/product.tracking.dto';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import axios from 'axios';
import { productInfo11st, xmlConvert11st } from 'src/utils/openapi.11st';

const REGEXP_11ST = /(?:http:\/\/|https:\/\/)?www\.11st\.co\.kr\/products\/[1-9]\d*(?:\/share)?/g;

@Injectable()
export class ProductService {
    constructor(
        @InjectRepository(TrackingProduct)
        private trackingProductRepository: Repository<TrackingProduct>,
    ) {}
    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductDetailsDto> {
        const { productUrl } = productUrlDto;
        if (!REGEXP_11ST.test(productUrl)) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
        try {
            const { pathname } = new URL(productUrl);
            const code = pathname.split('/')[2];
            const openApiUrl = productInfo11st(code);
            const xml = await axios.get(openApiUrl, { responseType: 'arraybuffer' });
            const productDetails = xmlConvert11st(xml.data);
            return {
                productCode: productDetails['ProductCode']['text'],
                productName: productDetails['ProductName']['text'],
                productPrice: productDetails['ProductPrice']['Price']['text'],
                shop: '11번가',
                imageUrl: productDetails['BasicImage']['text'],
            };
        } catch (e) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
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
