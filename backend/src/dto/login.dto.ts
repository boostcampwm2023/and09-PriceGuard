import { ApiProperty } from '@nestjs/swagger';
import { IsEmail, IsString } from 'class-validator';

export class LoginDto {
    @ApiProperty({
        example: 'bichoi0715@naver.com',
        description: '로그인 이메일',
        required: true,
    })
    @IsEmail()
    email: string;

    @ApiProperty({
        example: '1q2w3e4r',
        description: '로그인 비밀번호',
        required: true,
    })
    @IsString()
    password: string;

    @ApiProperty({
        example: 'firebase cloud messaging registeration token',
        description: 'FCM 등록 토큰',
        required: true,
    })
    @IsString()
    firebaseToken: string;
}
