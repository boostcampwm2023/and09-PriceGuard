import { Injectable } from '@nestjs/common';
import { UserDto } from '../dto/user.dto';
import { Repository } from 'typeorm';
import { User } from '../entities/user.entity';
import * as bcrypt from 'bcrypt';
import { InjectRepository } from '@nestjs/typeorm';

@Injectable()
export class UsersRepository extends Repository<User> {
    constructor(
        @InjectRepository(User)
        private readonly repository: Repository<User>,
    ) {
        super(repository.target, repository.manager, repository.queryRunner);
    }
    async createUser(userDto: UserDto): Promise<User> {
        const { email, userName, password } = userDto;
        const salt = await bcrypt.genSalt();
        const hashedPassword = await bcrypt.hash(password, salt);
        const user = User.create({ email, userName, password: hashedPassword });
        await user.save();
        return user;
    }
}
