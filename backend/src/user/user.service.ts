import { HttpException, HttpStatus, Inject, Injectable, forwardRef } from '@nestjs/common';
import { User } from '../entities/user.entity';
import { UsersRepository } from './user.repository';
import { InjectRepository } from '@nestjs/typeorm';
import { ValidationException } from '../exceptions/validation.exception';
import * as bcrypt from 'bcrypt';
import { CacheService } from 'src/cache/cache.service';
import { MailService } from 'src/mail/mail.service';
import { AuthService } from 'src/auth/auth.service';
import { UserRegisterDto } from 'src/dto/user.register.dto';
import { UserDto } from 'src/dto/user.dto';

@Injectable()
export class UsersService {
    constructor(
        @InjectRepository(UsersRepository)
        private usersRepository: UsersRepository,
        private cacheService: CacheService,
        private mailService: MailService,
        @Inject(forwardRef(() => AuthService))
        private authService: AuthService,
    ) {}

    async registerUser(userRegisterDto: UserRegisterDto): Promise<User> {
        const { userName, email, password, verificationCode } = userRegisterDto;
        await this.authService.verifyEmail(email, verificationCode);
        const userDto: UserDto = { userName, email, password };
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

    async sendVerificationEmail(email: string) {
        const user = await this.usersRepository.findOne({ where: { email } });
        if (user) {
            throw new HttpException('이메일 중복', HttpStatus.CONFLICT);
        }
        await this.mailService.sendVerficationCode(email);
    }

    async checkEmailVarifacted(email: string) {
        const user = await this.usersRepository.findOne({ where: { email } });
        if (!user) {
            throw new HttpException('해당 이메일의 사용자를 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        if (!user.verified) {
            return false;
        }
        return true;
    }
}
