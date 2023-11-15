import { HttpException, Injectable } from '@nestjs/common';
import { UserDto } from './dto/user.dto';
import { Repository } from 'typeorm';
import { User } from './user.entity';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UsersRepository extends Repository<User> {
    async createUser(userDto: UserDto): Promise<User> {
        const { email, username, password } = userDto;
        const salt = await bcrypt.genSalt();
        const hashedPassword = await bcrypt.hash(password, salt);
        const user = User.create({ email, username, password: hashedPassword });
        await user.save();
        return user;
    }
}
