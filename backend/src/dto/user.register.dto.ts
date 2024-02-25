import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsString } from 'class-validator';

export class UserRegisterDto {
    @ApiProperty({
        example: 'example123@naver.com',
        description: '사용자 이메일',
        required: true,
    })
    @IsEmail()
    email: string;

    @ApiProperty({
        example: '최병익',
        description: '사용자 이름',
        required: true,
    })
    @IsString()
    userName: string;

    @ApiProperty({
        example: '345123',
        description: '인증코드',
        required: true,
    })
    @IsString()
    verificationCode: string;

    @ApiProperty({
        example: '1q2w3e4r',
        description: '사용자 비밀번호',
        required: true,
    })
    @IsString()
    password: string;
}
