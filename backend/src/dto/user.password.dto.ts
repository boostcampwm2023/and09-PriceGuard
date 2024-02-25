import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString } from 'class-validator';

export class UserPasswordDto {
    @ApiProperty({
        example: '1q2w3e4r',
        description: '사용자 비밀번호',
        required: true,
    })
    @IsString()
    @IsNotEmpty()
    password: string;
}
