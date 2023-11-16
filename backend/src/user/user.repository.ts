import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { UserDto } from './dto/user.dto';
import { Repository } from 'typeorm';
import { User } from './user.entity';
import * as bcrypt from 'bcrypt';
import { ValidationException } from 'src/exceptions/validation.exception';

@Injectable()
export class UsersRepository extends Repository<User> {
    async createUser(userDto: UserDto): Promise<User> {
        const { email, userName, password } = userDto;
        const salt = await bcrypt.genSalt();
        const hashedPassword = await bcrypt.hash(password, salt);
        const user = User.create({ email, userName, password: hashedPassword });
        try {
            await user.save();
            return user;
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
}
