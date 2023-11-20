import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { UserDto } from '../dto/user.dto';
import { User } from '../entities/user.entity';
import { UsersRepository } from './user.repository';
import { InjectRepository } from '@nestjs/typeorm';
import { ValidationException } from 'src/exceptions/validation.exception';

@Injectable()
export class UsersService {
    constructor(
        @InjectRepository(UsersRepository)
        private usersRepository: UsersRepository,
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
}
