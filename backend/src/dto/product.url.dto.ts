import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class ProductUrlDto {
    @ApiProperty({
        example: 'http://www.11st.co.kr/products/5897533626/share',
        description: '상품 공유 링크',
        required: true,
    })
    @IsString()
    productUrl: string;
}
