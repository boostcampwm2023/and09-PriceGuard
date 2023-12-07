import { HttpException, HttpStatus, Inject, Injectable, forwardRef } from '@nestjs/common';
import { UsersService } from 'src/user/user.service';
import * as bcrypt from 'bcrypt';
import { User } from 'src/entities/user.entity';
import { ValidationException } from 'src/exceptions/validation.exception';
import { JwtService } from '@nestjs/jwt';
import { ACCESS_TOKEN_SECRETS, REFRESH_TOKEN_SECRETS } from 'src/constants';
import { InjectRedis } from '@songkeys/nestjs-redis';
import Redis from 'ioredis';

@Injectable()
export class AuthService {
    constructor(
        @Inject(forwardRef(() => UsersService)) private usersService: UsersService,
        private readonly jwtService: JwtService,
        @InjectRedis() private readonly redis: Redis,
    ) {}

    async getAccessToken(user: User): Promise<string> {
        return this.jwtService.sign(
            { id: user.id, email: user.email, name: user.userName },
            { secret: ACCESS_TOKEN_SECRETS, expiresIn: '2h' },
        );
    }

    async getRefreshToken(user: User): Promise<string> {
        const refreshToken = this.jwtService.sign({ id: user.id }, { secret: REFRESH_TOKEN_SECRETS, expiresIn: '2w' });
        await this.redis.set(`refreshToken:${user.id}`, refreshToken);
        return refreshToken;
    }

    async validateUser(email: string, password: string): Promise<Record<string, string>> {
        const user = await this.usersService.findOne(email);
        if (!user || !bcrypt.compareSync(password, user.password)) {
            throw new ValidationException('로그인 실패');
        }
        return {
            accessToken: await this.getAccessToken(user),
            refreshToken: await this.getRefreshToken(user),
        };
    }

    async refreshJWT(userId: string): Promise<Record<string, string>> {
        const user = await this.usersService.getUserById(userId);
        if (!user) {
            throw new HttpException('유효하지 않은 refreshToken', HttpStatus.BAD_REQUEST);
        }
        return {
            accessToken: await this.getAccessToken(user),
            refreshToken: await this.getRefreshToken(user),
        };
    }

    async addFirebaseToken(userId: string, token: string) {
        try {
            await this.redis.set(`firebaseToken:${userId}`, token);
            return token;
        } catch (e) {
            throw new HttpException('Firebase 토큰 등록 실패', HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
