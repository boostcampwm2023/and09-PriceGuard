import { BASE_URL_11ST, OPEN_API_KEY_11ST } from 'src/constants';
import * as convert from 'xml-js';
import * as iconv from 'iconv-lite';

export function xmlConvert11st(xml: Buffer) {
    const xmlUtf8 = iconv.decode(xml, 'EUC-KR').toString();
    const {
        ProductInfoResponse: { Product },
    }: convert.ElementCompact = convert.xml2js(xmlUtf8, {
        compact: true,
        cdataKey: 'text',
        textKey: 'text',
    });
    return Product;
}

export function productInfo11st(productCode: string) {
    const shopUrl = new URL(BASE_URL_11ST);
    shopUrl.searchParams.append('key', OPEN_API_KEY_11ST);
    shopUrl.searchParams.append('productCode', productCode);
    return shopUrl.toString();
}
