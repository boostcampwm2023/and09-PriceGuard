import { InjectRepository } from '@nestjs/typeorm';
import { JWTRepository } from './jwt.repository';
import { Token } from 'src/entities/token.entity';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { INVALIDATED_REFRESHTOKEN, REFRESH_TOKEN_SECRETS } from 'src/constants';

@Injectable()
export class JWTService {
    constructor(
        @InjectRepository(JWTRepository)
        private jwtRepository: JWTRepository,
        private jwtService: JwtService,
    ) {}

    async validateRefreshToken(userId: string, payload: string) {
        if (!(await this.isLatestRefreshToken(userId, payload))) {
            this.jwtRepository.saveToken(userId, INVALIDATED_REFRESHTOKEN);
            throw new UnauthorizedException('이미 재발급된 refreshToken');
        }
    }

    async isLatestRefreshToken(userId: string, payload: string) {
        const tokenInfo = await this.findOne(userId);
        const latestRefreshToken = tokenInfo?.token;
        const token = this.jwtService.sign(payload, { secret: REFRESH_TOKEN_SECRETS });
        if (latestRefreshToken !== token) {
            return false;
        }
        return true;
    }

    async findOne(userId: string): Promise<Token | null> {
        const refreshToken = await this.jwtRepository.findOne({ where: { userId: userId } });
        return refreshToken;
    }
}
