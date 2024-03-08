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
    @ApiProperty({
        example: 'access example',
        description: 'Access Token의 만료 기간은 2시간이다.',
    })
    accessToken: string;
    @ApiProperty({
        example: 'refresh example',
        description: 'Refresh Token의 만료 기간은 2주이다.',
    })
    refreshToken: string;
}

export class VerifyEmailSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이메일 인증 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: 'verify token example',
        description: 'Verify Token의 만료 기간은 3분이다.',
    })
    verifyToken: string;
}

export class InvalidCodeError {
    @ApiProperty({
        example: HttpStatus.UNAUTHORIZED,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '유효하지 않은 인증 코드.',
        description: '메시지',
    })
    message: string;
}
