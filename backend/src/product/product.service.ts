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

const REGEXP_11ST = /http[s]?:\/\/(?:www\.|m\.)?11st\.co\.kr\/products\/(?:ma\/|m\/)?([1-9]\d*)(?:\?.*)?(?:\/share)?/;
@Injectable()
export class ProductService {
    constructor(
        @InjectRepository(TrackingProduct)
        private trackingProductRepository: Repository<TrackingProduct>,
    ) {}
    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductDetailsDto> {
        const { productUrl } = productUrlDto;
        const matchList = productUrl.match(REGEXP_11ST);
        if (matchList === null) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
        try {
            const [, code] = matchList;
            const openApiUrl = productInfo11st(code);
            const xml = await axios.get(openApiUrl, { responseType: 'arraybuffer' });
            const productDetails = xmlConvert11st(xml.data);
            const price = productDetails['ProductPrice']['Price']['text'].replace(/(원|,)/g, '');
            return {
                productCode: productDetails['ProductCode']['text'],
                productName: productDetails['ProductName']['text'],
                productPrice: parseInt(price),
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
        const trackingListInfo = trackingProductList.map(({ product, targetPrice }) => {
            const { productName, productCode, shop, shopUrl, imageUrl } = product;
            return {
                productName,
                productCode,
                shop,
                shopUrl,
                imageUrl,
                targetPrice: targetPrice,
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
