import { HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import { ProductDetailsDto } from './product.details.dto';
import { TrackingProductDto } from './product.tracking.dto';

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
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '존재하지 않는 상품 코드입니다.',
        description: '메시지',
    })
    message: string;
}

const productListExample = [
    {
        productName: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        productCode: '5897533626',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700527038699',
    },
    {
        productName: 'Mercer Culinary 밀레니아 10인치 브레드 나이프 빵 칼 (M23210WBH)',
        productCode: '3534429539',
        shop: '11번가',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B01HZ0YT2C/B.jpg?1700390686058',
    },
];

const trackingProductListExample = [
    {
        productName: 'Hallmark Keepsake 해리포터 마법의 분류 모자 크리스마스 장식',
        productCode: '5897533626',
        shop: '11번가',
        shopUrl: 'https://www.11st.co.kr/products/6221602897',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B091516D2Z/B.jpg?1700527038699',
        targetPrice: 30000,
        price: 20000,
    },
    {
        productName: 'Mercer Culinary 밀레니아 10인치 브레드 나이프 빵 칼 (M23210WBH)',
        productCode: '3534429539',
        shop: '11번가',
        shopUrl: 'https://www.11st.co.kr/products/6221602897',
        imageUrl: 'https://cdn.011st.com/11dims/strip/false/11src/asin/B01HZ0YT2C/B.jpg?1700390686058',
        targetPrice: 20000,
        price: 15000,
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
        example: JSON.stringify(productListExample, null, 2),
        description: '추천 상품 목록',
    })
    recommendList: ProductDetailsDto[];
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
        example: '삭제할 상품을 찾을 수 없습니다.',
        description: '메시지',
    })
    message: string;
}
