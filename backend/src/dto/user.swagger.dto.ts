import { HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';

export class RegisterSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '회원 가입 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: 'access example',
        description: 'Access Token의 만료 기간은 5분이다.',
    })
    accessToken: string;
    @ApiProperty({
        example: 'refresh example',
        description: 'Refresh Token의 만료 기간은 2주이다.',
    })
    refreshToken: string;
}

export class LoginSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '로그인 성공',
        description: '메시지',
    })
    message: string;
    @ApiProperty({
        example: 'access example',
        description: 'Access Token의 만료 기간은 5분이다.',
    })
    accessToken: string;
    @ApiProperty({
        example: 'refresh example',
        description: 'Refresh Token의 만료 기간은 2주이다.',
    })
    refreshToken: string;
}

export class DupEmailError {
    @ApiProperty({
        example: HttpStatus.CONFLICT,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이메일 중복',
        description: '메시지',
    })
    message: string;
}

export class BadRequestError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '입력 값이 유효하지 않습니다',
        description: '메시지',
    })
    message: string;
}

export class LoginFailError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '로그인 실패',
        description: '메시지',
    })
    message: string;
}

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
