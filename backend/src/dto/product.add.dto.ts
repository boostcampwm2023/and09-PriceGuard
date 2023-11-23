import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class ProductAddDto {
    @ApiProperty({
        example: '5897533626',
        description: '상품 코드',
        required: true,
    })
    @IsString()
    @IsNotEmpty()
    productCode: string;
    @ApiProperty({
        example: 36000,
        description: '목표 가격',
        required: true,
    })
    @IsNumber()
    @IsNotEmpty()
    targetPrice: number;
}
