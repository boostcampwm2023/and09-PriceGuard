import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class UserEmailDto {
    @ApiProperty({
        example: 'bichoi0715@naver.com',
        description: '인증을 위한 이메일',
        required: true,
    })
    @IsString()
    @IsNotEmpty()
    email: string;
}
