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
        description: 'Access Token의 만료 기간은 2시간이다.',
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
        description: 'Access Token의 만료 기간은 2시간이다.',
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

export class FirebaseTokenSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: 'FCM 토큰 등록 성공',
        description: '메시지',
    })
    message: string;
}

export class RemoveSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '회원탈퇴 성공',
        description: '메시지',
    })
    message: string;
}

export class RemoveFailError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '회원탈퇴 실패',
        description: '메시지',
    })
    message: string;
}

export class SendVerificationEmailSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이메일 인증 코드 발송 성공',
        description: '메시지',
    })
    message: string;
}

export class SendVerificationEmailError {
    @ApiProperty({
        example: HttpStatus.BAD_REQUEST,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이메일 인증 코드 발송실패',
        description: '메시지',
    })
    message: string;
}

export class TooManySendEmailError {
    @ApiProperty({
        example: HttpStatus.TOO_MANY_REQUESTS,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '이메일 발송 하루 최대 횟수 초과',
        description: '메시지',
    })
    message: string;
}

export class InvalidVerificationCode {
    @ApiProperty({
        example: HttpStatus.UNAUTHORIZED,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '유효하지 않은 인증 코드',
        description: '메시지',
    })
    message: string;
}

export class CheckEmailVerificatedSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '사용자 이메일 인증 여부 조회 성공',
        description: '메시지',
    })
    message: string;
}

export class EmailNotFound {
    @ApiProperty({
        example: HttpStatus.NOT_FOUND,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '해당 이메일을 찾을 수 없습니다.',
        description: '메시지',
    })
    message: string;
}

export class ChangePasswordSuccess {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: '사용자 비밀번호 변경 성공',
        description: '메시지',
    })
    message: string;
}
