import { HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';

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

export class AddProductError {
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
