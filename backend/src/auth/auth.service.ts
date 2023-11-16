import { Injectable } from '@nestjs/common';
import { UsersService } from 'src/user/user.service';
import * as bcrypt from 'bcrypt';
import { User } from 'src/user/user.entity';
import { ValidationException } from 'src/exceptions/validation.exception';

@Injectable()
export class AuthService {
    constructor(private usersService: UsersService) {}

    async validateUser(email: string, password: string): Promise<User> {
        const user = await this.usersService.findOne(email);
        if (!user || !bcrypt.compareSync(password, user.password)) {
            throw new ValidationException('로그인 실패');
        }
        return user;
    }
}
