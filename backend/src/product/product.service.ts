import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { ProductUrlDto } from '../dto/product.url.dto';
import { ProductDto } from '../dto/product.dto';
import { ProductDetailsDto } from 'src/dto/product.details.dto';
import axios from 'axios';
import { openApiUrl11st, xmlParse11st } from 'src/utils/openapi.11st';
const SHOP_URL_11ST = 'www.11st.co.kr';
@Injectable()
export class ProductService {
    async verifyUrl(productUrlDto: ProductUrlDto): Promise<ProductDetailsDto> {
        const { productUrl } = productUrlDto;
        const { hostname, pathname } = new URL(productUrl);
        const [, keyword, code] = pathname.split('/');
        if (hostname !== SHOP_URL_11ST || keyword !== 'products')
            throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
        const openApiUrl = openApiUrl11st(code);
        const xml = await axios.get(openApiUrl, { responseType: 'arraybuffer' });
        const productDetails = xmlParse11st(xml.data);
        const productCode = productDetails['ProductCode']['text'];
        const productName = productDetails['ProductName']['text'];
        const shop = '11번가';
        const imageUrl = productDetails['BasicImage']['text'];
        return { productCode, productName, shop, imageUrl };
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
