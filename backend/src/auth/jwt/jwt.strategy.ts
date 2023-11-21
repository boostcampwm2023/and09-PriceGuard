import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { ACCESS_TOKEN_SECRETS, REFRESH_TOKEN_SECRETS } from 'src/constants';

@Injectable()
export class AccessJwtStrategy extends PassportStrategy(Strategy, 'access') {
    constructor() {
        super({
            jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
            ignoreExpiration: true,
            secretOrKey: ACCESS_TOKEN_SECRETS,
        });
    }

    async validate(payload: any) {
        if (Date.now() >= payload.exp * 1000) {
            throw new HttpException('토큰이 만료되었습니다.', HttpStatus.GONE);
        }
        return { email: payload.email, userName: payload.name };
    }
}

@Injectable()
export class RefreshJwtStrategy extends PassportStrategy(Strategy, 'refresh') {
    constructor() {
        super({
            jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
            ignoreExpiration: true,
            secretOrKey: REFRESH_TOKEN_SECRETS,
        });
    }

    async validate(payload: any) {
        if (Date.now() >= payload.exp * 1000) {
            throw new HttpException('토큰이 만료되었습니다.', HttpStatus.GONE);
        }
        // TODO: refreshToken이 탈취당한 경우 보안 처리를 위해, 지금 까지 발급된 refreshToken인지 조회 필요
        return { id: payload.id };
    }
}
