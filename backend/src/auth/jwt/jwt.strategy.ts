import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy, ExtractJwt } from 'passport-jwt';
import { ACCESS_TOKEN_SECRETS } from 'src/constants';

@Injectable()
export class AccessJwtStrategy extends PassportStrategy(Strategy) {
    constructor() {
        super({
            jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
            ignoreExpiration: false,
            secretOrKey: ACCESS_TOKEN_SECRETS,
        });
    }

    async validate(payload: any) {
        return { userId: payload.email, username: payload.name };
    }
}
