import { HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';

export class AuthSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '사용자 인증 성공',
        description: '메시지',
    })
    message: string;
}

export class ExpiredTokenError {
    @ApiProperty({
        example: HttpStatus.GONE,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '토큰이 만료되었습니다.',
        description: '메시지',
    })
    message: string;
}

export class InvalidTokenError {
    @ApiProperty({
        example: HttpStatus.UNAUTHORIZED,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: 'Unauthorized',
        description: '메시지',
    })
    message: string;
}

export class RefreshJWTSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '토큰 재발급 성공',
        description: '메시지',
    })
    message: string;
}
