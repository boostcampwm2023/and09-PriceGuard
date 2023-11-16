import { Injectable } from '@nestjs/common';
import { UsersService } from 'src/user/user.service';

@Injectable()
export class AuthService {
    constructor(private usersService: UsersService) {}

    async validateUser(email: string, password: string): Promise<any> {
        const user = await this.usersService.findOne(email);
    }
}
