import { Inject, Injectable, forwardRef } from '@nestjs/common';
import { UsersService } from 'src/user/user.service';
import * as bcrypt from 'bcrypt';
import { User } from 'src/entities/user.entity';
import { ValidationException } from 'src/exceptions/validation.exception';
import { JwtService } from '@nestjs/jwt';
import { ACCESS_TOKEN_SECRETS, REFRESH_TOKEN_SECRETS } from 'src/constants';
@Injectable()
export class AuthService {
    constructor(
        @Inject(forwardRef(() => UsersService)) private usersService: UsersService,
        private readonly jwtService: JwtService,
    ) {}

    async getAccessToken(user: User): Promise<string> {
        return this.jwtService.sign(
            { email: user.email, name: user.userName },
            { secret: ACCESS_TOKEN_SECRETS, expiresIn: '5m' },
        );
    }

    async getRefreshToken(user: User): Promise<string> {
        return this.jwtService.sign({ id: user.id }, { secret: REFRESH_TOKEN_SECRETS, expiresIn: '2w' });
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
}
