import { BASE_URL_11ST, OPEN_API_KEY_11ST } from 'src/constants';
import * as convert from 'xml-js';
import * as iconv from 'iconv-lite';
import axios from 'axios';
import { HttpException, HttpStatus } from '@nestjs/common';
import { ProductInfoDto } from 'src/dto/product.info.dto';

function xmlConvert11st(xml: Buffer) {
    const xmlUtf8 = iconv.decode(xml, 'EUC-KR').toString();
    const {
        ProductInfoResponse: { Product, ProductOption },
    }: convert.ElementCompact = convert.xml2js(xmlUtf8, {
        compact: true,
        cdataKey: 'text',
        textKey: 'text',
    });
    Product['isSoldOut'] = ProductOption ? ProductOption['Status'] === 'N' : false;
    return Product;
}

function productInfoUrl11st(productCode: string) {
    const shopUrl = new URL(BASE_URL_11ST);
    shopUrl.searchParams.append('key', OPEN_API_KEY_11ST);
    shopUrl.searchParams.append('productCode', productCode);
    shopUrl.searchParams.append('options', 'PdOption');
    return shopUrl.toString();
}

export async function getProductInfo11st(productCode: string, productId?: string): Promise<ProductInfoDto> {
    const openApiUrl = productInfoUrl11st(productCode);
    try {
        const xml = await axios.get(openApiUrl, { responseType: 'arraybuffer' });
        const productDetails = xmlConvert11st(xml.data);
        const price = productDetails['ProductPrice']['LowestPrice']['text'].replace(/(원|,)/g, '');
        return {
            productCode: productDetails['ProductCode']['text'],
            productName: productDetails['ProductName']['text'],
            productPrice: parseInt(price),
            shop: '11번가',
            imageUrl: productDetails['BasicImage']['text'],
            isSoldOut: productDetails['isSoldOut'],
            productId,
        };
    } catch (e) {
        throw new HttpException('존재하지 않는 상품 코드입니다.', HttpStatus.BAD_REQUEST);
    }
}

export function createUrl11st(productCode: string) {
    return `http://www.11st.co.kr/products/${productCode}/share`;
}
