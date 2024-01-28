import { HttpException, HttpStatus } from '@nestjs/common';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { BASE_URL_11ST, BROWSER_VERSION_20, OPEN_API_KEY_11ST, REGEX_SHOP } from 'src/constants';
import { JSDOM } from 'jsdom';
import * as convert from 'xml-js';
import * as iconv from 'iconv-lite';
import axios from 'axios';
import * as randomUseragent from 'random-useragent';
import { ProductIdentifierDto } from 'src/dto/product.identifier';

export async function getProductInfo(shop: string, productCode: string): Promise<ProductInfoDto> {
    if (shop === '11번가') {
        return await getProductInfo11st(productCode);
    }
    if (shop === 'SmartStore') {
        return await getProductInfoByBrandSmartStore(productCode);
    }
    throw new HttpException('존재하지 않는 상품입니다.', HttpStatus.BAD_REQUEST);
}

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

async function getProductInfo11st(productCode: string): Promise<ProductInfoDto> {
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
        };
    } catch (e) {
        throw new HttpException('존재하지 않는 상품 코드입니다.', HttpStatus.BAD_REQUEST);
    }
}

async function getProductInfoByBrandSmartStore(productCode: string): Promise<ProductInfoDto> {
    const userAgent = randomUseragent.getRandom(function (ua) {
        return parseFloat(ua.browserVersion) >= BROWSER_VERSION_20;
    });
    const instance = axios.create();
    const smartstoreURL = `https://smartstore.naver.com/main/products/${productCode}`;
    instance.interceptors.request.use((req: any) => {
        req.headers.set(
            'Accept',
            'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        );
        req.headers.set('Accept-Encoding', 'gzip, deflate, br');
        req.headers.set('Accept-Language', 'en');
        req.headers.set('Cache-Control', 'no-cache');
        req.headers.set('Dnt', '1');
        req.headers.set('Pragma', 'no-cache');
        req.headers.set('Sec-Ch-Ua', '"(Not(A:Brand";v="8", "Chromium";v="98"');
        req.headers.set('Sec-Ch-Ua-Mobile', '?0');
        req.headers.set('Sec-Ch-Ua-Platform', '"macOS"');
        req.headers.set('Sec-Fetch-Dest', 'document');
        req.headers.set('Sec-Fetch-Mode', 'navigate');
        req.headers.set('Sec-Fetch-Site', 'none');
        req.headers.set('Sec-Fetch-User', '?1');
        req.headers.set('Upgrade-Insecure-Requests', '1');
        req.headers.set('User-Agent', userAgent);
        return req;
    });
    try {
        const rawHtml = await instance.get(smartstoreURL);
        const dom = new JSDOM(rawHtml.data);
        const document = dom.window.document;
        const head = document.head;
        const scripts = head.getElementsByTagName('script');
        const scriptArray = Array.from(scripts);
        let jsonLD = null;
        for (let i = 0; i < scriptArray.length; i++) {
            if (scriptArray[i].getAttribute('type') === 'application/ld+json') {
                jsonLD = JSON.parse(scriptArray[i].innerHTML);
                break;
            }
        }
        if (jsonLD === null) {
            throw new HttpException('존재하지 않는 상품 코드입니다.', HttpStatus.BAD_REQUEST);
        }
        return {
            productName: jsonLD['name'],
            productCode: jsonLD['productID'],
            productPrice: jsonLD['offers']['price'],
            shop: 'SmartStore',
            imageUrl: jsonLD['image'],
            isSoldOut: jsonLD['offers']['availability'] !== 'http://schema.org/InStock',
        };
    } catch (e) {
        throw new HttpException('존재하지 않는 상품 코드입니다.', HttpStatus.BAD_REQUEST);
    }
}

export function createUrl(shop: string, productCode: string) {
    if (shop === '11번가') {
        return `https://www.11st.co.kr/products/${productCode}/share`;
    }
    if (shop === 'SmartStore') {
        return `https://smartstore.naver.com/main/products/${productCode}`;
    }
}

export function identifyProductByUrl(productUrl: string): ProductIdentifierDto {
    let matchList = null;
    if ((matchList = productUrl.match(REGEX_SHOP['11ST']))) {
        return { shop: '11번가', productCode: matchList[1] };
    }
    if (
        (matchList = productUrl.match(REGEX_SHOP.NaverBrand)) ||
        (matchList = productUrl.match(REGEX_SHOP.NaverSmartStore))
    ) {
        return { shop: 'SmartStore', productCode: matchList[1] };
    }
    throw new HttpException('URL이 유효하지 않습니다.', HttpStatus.BAD_REQUEST);
}
