import { Body, Controller, Post, HttpStatus, UseFilters, UsePipes, forwardRef, Inject } from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from './dto/user.dto';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { UserValidationPipe } from 'src/exceptions/validation.user.pipe';
import { AuthService } from '../auth/auth.service';
import { LoginDto } from '../auth/dto/login.dto';

@Controller('user')
@UseFilters(UserExceptionFilter)
export class UsersController {
    constructor(
        private userService: UsersService,
        @Inject(forwardRef(() => AuthService))
        private authService: AuthService,
    ) {}

    @Post('register')
    @UsePipes(new UserValidationPipe())
    async registerUser(
        @Body() userDto: UserDto,
    ): Promise<{ statusCode: number; message: string; accessToken: string; refreshToken: string }> {
        const user = await this.userService.registerUser(userDto);
        const accessToken = await this.authService.getAccessToken(user);
        const refreshToken = await this.authService.getRefreshToken(user);
        return { statusCode: HttpStatus.OK, message: '회원가입 성공', accessToken, refreshToken };
    }

    @Post('login')
    @UsePipes(new UserValidationPipe())
    async loginUser(
        @Body() loginDto: LoginDto,
    ): Promise<{ statusCode: number; message: string; accessToken: string; refreshToken: string }> {
        const { email, password } = loginDto;
        const { accessToken, refreshToken } = await this.authService.validateUser(email, password);
        return { statusCode: HttpStatus.OK, message: '로그인 성공', accessToken, refreshToken };
    }
}
