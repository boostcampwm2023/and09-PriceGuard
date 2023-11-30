import { HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import { TrackingProductDto } from './product.tracking.dto';
import { ProductPriceDto } from './product.price.dto';
import { RecommendProductDto } from './product.recommend.dto';

export class VerifyUrlSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 URL 검증 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        description: '상품명',
    })
    productName: string;
    @ApiProperty({
        example: '5897533626',
        description: '상품 코드',
    })
    productCode: string;
    @ApiProperty({
        example: '53730',
        description: '상품 가격',
    })
    productPrice: number;
    @ApiProperty({
        example: '11번가',
        description: '쇼핑몰 이름',
    })
    shop: string;
    @ApiProperty({
        example: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700527038699',
        description: '상품 이미지 URL',
    })
    imageUrl: string;
}

export class UnauthorizedRequest {
    @ApiProperty({
        example: HttpStatus.UNAUTHORIZED,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '승인되지 않은 요청입니다.',
        description: '메시지',
    })
    message: string;
}

export class UrlError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: 'URL이 유효하지 않습니다.',
        description: '메시지',
    })
    message: string;
}
export class AddProductSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 추가 성공',
        description: '메시지',
    })
    message: string;
}

export class AddProductConflict {
    @ApiProperty({
        example: HttpStatus.CONFLICT,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이미 등록된 상품입니다.',
        description: '메시지',
    })
    message: string;
}

export class RequestError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '잘못된 요청입니다.',
        description: '메시지',
    })
    message: string;
}

export class ProductCodeError {
    @ApiProperty({
        example: HttpStatus.NOT_FOUND,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '존재하지 않는 상품 코드입니다.',
        description: '메시지',
    })
    message: string;
}

const priceDataExample = [
    {
        time: 1701212844.919,
        price: 1211353123,
        isSoldOut: false,
    },
    {
        time: 1701212846.442,
        price: 1211353123,
        isSoldOut: false,
    },
];

const trackingProductListExample = [
    {
        productName: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        productCode: '5897533626',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700527038699',
        targetPrice: 30000,
        price: 20000,
        priceData: priceDataExample,
    },
    {
        productName: 'Mercer Culinary 밀레니아 10인치 브레드 나이프 빵 칼 (M23210WBH)',
        productCode: '3534429539',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B01HZ0YT2C/B.jpg?1700390686058',
        targetPrice: 20000,
        price: 15000,
        priceData: priceDataExample,
    },
];

export class GetTrackingListSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 목록 조회 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: JSON.stringify(trackingProductListExample, null, 2),
        description: '상품 목록',
    })
    trackingList: TrackingProductDto[];
}

const recommendProductListExample = [
    {
        productName: '갤럭시 GALAX 지포스 RTX 4060 1X D6 8GB',
        productCode: '6221602897',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/product/6221602897/B.jpg?556000000',
        price: 1234,
        rank: 1,
        priceData: priceDataExample,
    },
    {
        productName: '본사) 쿠쿠 화이트 3구 인덕션레인지 CIR-E301FW',
        productCode: '3969500068',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/dl/v2/5/0/0/0/6/8/pNOcE/3969500068_154126549.jpg',
        price: 1234,
        rank: 2,
        priceData: priceDataExample,
    },
    {
        productName:
            '[카드추가할인] 삼성전자 SL-T1670FW 잉크젯 플러스S 정품 무한 복합기 프린터 복사 팩스 스캔 WiFi 무선 지원 잉크포함',
        productCode: '4725944460',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/product/4725944460/B.jpg?338000000',
        price: 1234,
        rank: 3,
        priceData: priceDataExample,
    },
    {
        productName: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        productCode: '5897533626',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700715151392',
        price: 1234,
        rank: 4,
        priceData: priceDataExample,
    },
];

export class GetRecommendListSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '추천 상품 목록 조회 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: JSON.stringify(recommendProductListExample, null, 2),
        description: '추천 상품 목록',
    })
    recommendList: RecommendProductDto[];
}
export class ProductDetailsSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 상세 정보 조회 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        description: '상품명',
    })
    productName: string;
    @ApiProperty({
        example: '5897533626',
        description: '상품 코드',
    })
    productCode: string;
    @ApiProperty({
        example: '11번가',
        description: '쇼핑몰 이름',
    })
    shop: string;
    @ApiProperty({
        example: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700527038699',
        description: '상품 이미지 URL',
    })
    imageUrl: string;
    @ApiProperty({
        example: 1,
        description: '상품 순위',
    })
    rank: number;
    @ApiProperty({
        example: 'http://www.11st.co.kr/products/5897533626/share',
        description: '상품 링크',
    })
    shopUrl: string;
    @ApiProperty({
        example: '50000',
        description: '사용자 목표 가격',
    })
    targetPrice: number;
    @ApiProperty({
        example: '33730',
        description: '상품 역대 최저가',
    })
    lowestPrice: number;
    @ApiProperty({
        example: '53730',
        description: '상품 현재 가격',
    })
    price: number;
    @ApiProperty({
        example: JSON.stringify(priceDataExample, null, 2),
        description: '가격 그래프 데이터',
    })
    priceData: ProductPriceDto[];
}
export class ProductNotFound {
    @ApiProperty({
        example: HttpStatus.NOT_FOUND,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 목록을 찾을 수 없습니다.',
        description: '메시지',
    })
    message: string;
}

export class UpdateTargetPriceSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '목표 가격 수정 성공',
        description: '메시지',
    })
    message: string;
}

export class DeleteProductSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 삭제 성공',
        description: '메시지',
    })
    message: string;
}

export class ProductDetailsNotFound {
    @ApiProperty({
        example: HttpStatus.NOT_FOUND,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품 정보를 찾을 수 없습니다.',
        description: '메시지',
    })
    message: string;
}

export class TrackingProductsNotFound {
    @ApiProperty({
        example: HttpStatus.NOT_FOUND,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '상품을 찾을 수 없습니다.',
        description: '메시지',
    })
    message: string;
}
