import { InjectRepository } from '@nestjs/typeorm';
import { JWTRepository } from './jwt.repository';
import { Token } from 'src/entities/token.entity';
import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { REFRESH_TOKEN_SECRETS } from 'src/constants';

@Injectable()
export class JWTService {
    constructor(
        @InjectRepository(JWTRepository)
        private jwtRepository: JWTRepository,
        private jwtService: JwtService,
    ) {}

    async isLatestRefreshToken(userId: string, payload: string) {
        const tokenInfo = await this.findOne(userId);
        const latestRefreshToken = tokenInfo?.token;
        const token = this.jwtService.sign(payload, { secret: REFRESH_TOKEN_SECRETS });
        if (latestRefreshToken !== token) {
            throw new UnauthorizedException('이미 재발급된 refreshToken');
        }
        return true;
    }

    async findOne(userId: string): Promise<Token | null> {
        const refreshToken = await this.jwtRepository.findOne({ where: { userId } });
        return refreshToken;
    }
}
