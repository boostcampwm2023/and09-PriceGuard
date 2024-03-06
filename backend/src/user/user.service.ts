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

    async registerUserV1(userRegisterDto: UserRegisterDto): Promise<User> {
        const { userName, email, password, verificationCode } = userRegisterDto;
        await this.authService.verifyEmail(email, verificationCode);
        const userDto: UserDto = { userName, email, password, verified: true };
        return this.registerUser(userDto);
    }

    async findUserByEmail(email: string): Promise<User | null> {
        const user = await this.usersRepository.findOne({ where: { email } });
        return user;
    }

    async findUserById(userId: string): Promise<User | null> {
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

    async sendRegisterVerificationEmail(email: string) {
        const user = await this.usersRepository.findOne({ where: { email } });
        if (user) {
            throw new HttpException('이메일 중복', HttpStatus.CONFLICT);
        }
        await this.mailService.sendVerificationCode(email);
    }

    async sendVerificationEmail(email: string) {
        const user = await this.findUserByEmail(email);
        if (!user) {
            throw new HttpException('해당 이메일의 사용자를 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        await this.mailService.sendVerificationCode(email);
    }

    async checkEmailVerified(email: string) {
        const user = await this.findUserByEmail(email);
        if (!user) {
            throw new HttpException('해당 이메일의 사용자를 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        if (!user.verified) {
            return false;
        }
        return true;
    }

    async changePassword(email: string, password: string) {
        const user = await this.findUserByEmail(email);
        if (!user) {
            throw new ValidationException('비밀번호 변경 실패');
        }
        const salt = await bcrypt.genSalt();
        const hashedPassword = await bcrypt.hash(password, salt);
        user.password = hashedPassword;
        await this.usersRepository.save(user);
    }

    async verifyUserByEmail(email: string): Promise<void> {
        const user = await this.findUserByEmail(email);
        if (!user) {
            throw new HttpException('해당 이메일의 사용자를 찾을 수 없습니다.', HttpStatus.NOT_FOUND);
        }
        user.verified = true;
        await this.usersRepository.save(user);
    }
}
