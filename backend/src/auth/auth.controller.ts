import {
    ApiGoneResponse,
    ApiHeader,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { Controller, Get, HttpStatus, Req, UseFilters, UseGuards } from '@nestjs/common';
import { AuthExceptionFilter } from 'src/exceptions/auth.exception';
import { AuthSuccess, ExpiredTokenError, InvalidTokenError, RefreshJWTSuccess } from 'src/dto/auth.swagger.dto';
import { AuthGuard } from '@nestjs/passport';
import { AuthService } from './auth.service';
import { Request } from 'express';
import { User } from 'src/entities/user.entity';

@ApiTags('auth api')
@Controller('auth')
@UseFilters(AuthExceptionFilter)
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
}
