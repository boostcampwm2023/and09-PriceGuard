import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import axios from 'axios';
import { productInfo11st, xmlConvert11st } from 'src/utils/openapi.11st';

const REGEXP_11ST = /http[s]?:\/\/(?:www\.|m\.)?11st\.co\.kr\/products\/(?:ma\/|m\/)?([1-9]\d*)(?:\?.*)?(?:\/share)?/;
@Injectable()
export class ProductService {
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
