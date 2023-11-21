import { ApiProperty } from '@nestjs/swagger';

export class ProductDto {
    @ApiProperty({
        example: '5897533626',
        description: '상품 코드',
        required: true,
    })
    productCode: string;
    @ApiProperty({
        example: '36000',
        description: '목표 가격',
        required: true,
    })
    targetPrice: string;
}
