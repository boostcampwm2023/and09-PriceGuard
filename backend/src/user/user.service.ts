import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { UserDto } from '../dto/user.dto';
import { User } from '../entities/user.entity';
import { UsersRepository } from './user.repository';
import { InjectRepository } from '@nestjs/typeorm';
import { ValidationException } from '../exceptions/validation.exception';
import * as bcrypt from 'bcrypt';
import { CacheService } from 'src/cache/cache.service';

@Injectable()
export class UsersService {
    constructor(
        @InjectRepository(UsersRepository)
        private usersRepository: UsersRepository,
        private cacheService: CacheService,
    ) {}

    async registerUser(userDto: UserDto): Promise<User> {
        try {
            return await this.usersRepository.createUser(userDto);
        } catch (error) {
            if (error.code === 'ER_DUP_ENTRY') {
                throw new HttpException('Duplicate entry', HttpStatus.CONFLICT);
            }
            if (error.code === 'ER_NO_DEFAULT_FOR_FIELD') {
                throw new ValidationException('입력 값이 유효하지 않습니다');
            }
            throw new HttpException('Error creating user', HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    async findOne(email: string): Promise<User | null> {
        const user = await this.usersRepository.findOne({ where: { email } });
        return user;
    }

    async getUserById(userId: string): Promise<User | null> {
        return await this.usersRepository.findOne({ where: { id: userId } });
    }

    async removeUser(email: string, password: string) {
        const user = await this.usersRepository.findOne({ where: { email } });
        if (!user || !bcrypt.compareSync(password, user.password)) {
            throw new ValidationException('회원탈퇴 실패');
        }
        await this.cacheService.updateByRemoveUser(user.id);
        await this.usersRepository.remove(user);
    }
}
