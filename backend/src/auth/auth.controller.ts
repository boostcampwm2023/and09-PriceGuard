import {
    ApiBadRequestResponse,
    ApiBody,
    ApiGoneResponse,
    ApiHeader,
    ApiNotFoundResponse,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { Body, Controller, Get, HttpStatus, Post, Req, UseFilters, UseGuards } from '@nestjs/common';
import { HttpExceptionFilter } from 'src/exceptions/http.exception.filter';
import {
    AuthSuccess,
    ExpiredTokenError,
    InvalidCodeError,
    InvalidTokenError,
    RefreshJWTSuccess,
    VerifyEmailSuccess,
} from 'src/dto/auth.swagger.dto';
import { AuthGuard } from '@nestjs/passport';
import { AuthService } from './auth.service';
import { Request } from 'express';
import { User } from 'src/entities/user.entity';
import { VerifyEmailDto } from 'src/dto/auth.verify.email.dto';
import { RequestError } from 'src/dto/product.swagger.dto';
import { EmailNotFound } from 'src/dto/user.swagger.dto';

@ApiTags('auth api')
@Controller('auth')
@UseFilters(HttpExceptionFilter)
export class AuthController {
    constructor(private authService: AuthService) {}

    @ApiOperation({ summary: '인증 test API', description: '사용자가 인증 되어있는지 확인한다.' })
    @ApiHeader({
        name: 'Authorization Bearer Token',
        description: 'accessToken',
    })
    @ApiOkResponse({ type: AuthSuccess, description: '사용자 인증 성공' })
    @ApiGoneResponse({ type: ExpiredTokenError, description: 'accessToken 만료' })
    @ApiUnauthorizedResponse({ type: InvalidTokenError, description: '유효하지 않은 accessToken' })
    @Get('/authTest')
    @UseGuards(AuthGuard('access'))
    async isAuthenticated() {
        return { statusCode: HttpStatus.OK, message: '사용자 인증 성공' };
    }

    @ApiOperation({ summary: 'accessToken 재발급 API', description: 'accessTokend을 재발급한다.' })
    @ApiHeader({
        name: 'Authorization Bearer Token',
        description: 'refreshToken',
    })
    @ApiOkResponse({ type: RefreshJWTSuccess, description: 'JWT refresh 성공' })
    @ApiGoneResponse({ type: ExpiredTokenError, description: 'refreshToken 만료' })
    @ApiUnauthorizedResponse({ type: InvalidTokenError, description: '유효하지 않은 refreshToken' })
    @Get('/refreshJWT')
    @UseGuards(AuthGuard('refresh'))
    async refreshJWT(@Req() req: Request & { user: User }) {
        const { accessToken, refreshToken } = await this.authService.refreshJWT(req.user.id);
        return { statusCode: HttpStatus.OK, message: '토큰 재발급 성공', accessToken, refreshToken };
    }

    @ApiOperation({ summary: '이메일 인증 코드 검증', description: '이메일 인증 코드를 검증한다.' })
    @ApiOkResponse({ type: VerifyEmailSuccess, description: '이메일 인증 성공' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @ApiUnauthorizedResponse({ type: InvalidCodeError, description: '유효하지 않은 인증코드' })
    @ApiNotFoundResponse({ type: EmailNotFound, description: '해당 이메일로 발급받은 코드가 없거나 만료됨' })
    @ApiBody({ type: VerifyEmailDto })
    @Post('/verify/email')
    async verifyEmail(@Body() verifyEmailDto: VerifyEmailDto) {
        await this.authService.verifyEmail(verifyEmailDto.email, verifyEmailDto.verificationCode);
        await this.authService.verifyUserByEmail(verifyEmailDto.email);
        const verifyToken = await this.authService.getVerifyToken(verifyEmailDto.email);
        return { statusCode: HttpStatus.OK, message: '이메일 인증 성공', verifyToken };
    }
}
