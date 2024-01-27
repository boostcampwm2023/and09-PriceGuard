import axios from 'axios';
import { JSDOM } from 'jsdom';
import { ProductInfoDto } from 'src/dto/product.info.dto';
import { HttpException, HttpStatus } from '@nestjs/common';
import * as randomUseragent from 'random-useragent';
import { BROWSER_VERSION_20 } from 'src/constants';

export async function getProductInfoByBrandSmartStore(
    productCode: string,
    productId?: string,
): Promise<ProductInfoDto> {
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
            productId,
        };
    } catch (e) {
        throw new HttpException('존재하지 않는 상품 코드입니다.', HttpStatus.BAD_REQUEST);
    }
}
