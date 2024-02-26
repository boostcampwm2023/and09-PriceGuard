import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class VerifyEmailDto {
    @ApiProperty({
        example: 'example123@naver.com',
        description: '인증을 위한 이메일',
        required: true,
    })
    @IsString()
    @IsNotEmpty()
    email: string;

    @ApiProperty({
        example: '345123',
        description: '인증코드',
        required: true,
    })
    @IsString()
    verificationCode: string;
}
