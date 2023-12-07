import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { INVALIDATED_REFRESHTOKEN, REFRESH_TOKEN_SECRETS } from 'src/constants';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';

@Injectable()
export class JWTService {
    constructor(
        private jwtService: JwtService,
        @InjectRedis() private readonly redis: Redis,
    ) {}

    async validateRefreshToken(userId: string, payload: string) {
        if (!(await this.isLatestRefreshToken(userId, payload))) {
            await this.redis.set(`refreshToken:${userId}`, JSON.stringify({ INVALIDATED_REFRESHTOKEN }));
            throw new UnauthorizedException('이미 재발급된 refreshToken');
        }
    }

    async isLatestRefreshToken(userId: string, payload: string) {
        const tokenInfo = await this.findOne(userId);
        const latestRefreshToken = tokenInfo;
        const token = this.jwtService.sign(payload, { secret: REFRESH_TOKEN_SECRETS });
        if (latestRefreshToken !== token) {
            return false;
        }
        return true;
    }

    async findOne(userId: string): Promise<string | null> {
        const refreshToken = await this.redis.get(`refreshToken:${userId}`);
        return refreshToken;
    }
}
