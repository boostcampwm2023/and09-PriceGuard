import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { ACCESS_TOKEN_SECRETS, REFRESH_TOKEN_SECRETS } from 'src/constants';
import { JWTService } from './jwt.service';

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
        return { id: payload.id, email: payload.email, userName: payload.name };
    }
}

@Injectable()
export class RefreshJwtStrategy extends PassportStrategy(Strategy, 'refresh') {
    constructor(private jwtService: JWTService) {
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
        await this.jwtService.isLatestRefreshToken(payload.id, payload);
        return { id: payload.id };
    }
}
