import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import axios from 'axios';
import { openApiUrl11st, xmlParse11st } from 'src/utils/openapi.11st';

const REGEXP_11ST = /(?:http:\/\/|https:\/\/)?www\.11st\.co\.kr\/products\/[1-9]\d*(?:\/share)?/g;

@Injectable()
export class ProductService {
    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductDetailsDto> {
        const { productUrl } = productUrlDto;
        if (!REGEXP_11ST.test(productUrl)) {
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        }
        try {
            const { pathname } = new URL(productUrl);
            const code = pathname.split('/')[2];
            const openApiUrl = openApiUrl11st(code);
            const xml = await axios.get(openApiUrl, { responseType: 'arraybuffer' });
            const productDetails = xmlParse11st(xml.data);
            console.log(productDetails);
            return {
                productCode: productDetails['ProductCode']['text'],
                productName: productDetails['ProductName']['text'],
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

    getTrackingList() {}

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
