import { Body, Controller, Post, HttpStatus, UseFilters, UsePipes, forwardRef, Inject } from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from '../dto/user.dto';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { UserValidationPipe } from 'src/exceptions/validation.user.pipe';
import { AuthService } from '../auth/auth.service';
import { LoginDto } from '../dto/login.dto';
import {
    ApiBadRequestResponse,
    ApiBody,
    ApiConflictResponse,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
} from '@nestjs/swagger';
import { BadRequestError, DupEmailError, LoginFailError, LoginSuccess, RegisterSuccess } from 'src/dto/swagger.dto';

@ApiTags('사용자 API')
@Controller('user')
@UseFilters(UserExceptionFilter)
export class UsersController {
    constructor(
        private userService: UsersService,
        @Inject(forwardRef(() => AuthService))
        private authService: AuthService,
    ) {}

    @ApiOperation({ summary: '회원가입 API', description: '서버에게 회원가입 요청을 보낸다.' })
    @ApiBody({ type: UserDto })
    @ApiOkResponse({ type: RegisterSuccess, description: '회원가입 성공' })
    @ApiBadRequestResponse({ type: BadRequestError, description: '유효하지 않은 입력 값' })
    @ApiConflictResponse({ type: DupEmailError, description: '이메일 중복' })
    @Post('register')
    @UsePipes(new UserValidationPipe())
    async registerUser(@Body() userDto: UserDto): Promise<RegisterSuccess> {
        const user = await this.userService.registerUser(userDto);
        const accessToken = await this.authService.getAccessToken(user);
        const refreshToken = await this.authService.getRefreshToken(user);
        return { statusCode: HttpStatus.OK, message: '회원가입 성공', accessToken, refreshToken };
    }

    @ApiOperation({ summary: '로그인 API', description: '서버에게 로그인 요청을 보낸다.' })
    @ApiBody({ type: LoginDto })
    @ApiOkResponse({ type: LoginSuccess, description: '로그인 성공' })
    @ApiBadRequestResponse({ type: LoginFailError, description: '로그인 실패' })
    @Post('login')
    @UsePipes(new UserValidationPipe())
    async loginUser(@Body() loginDto: LoginDto): Promise<LoginSuccess> {
        const { email, password } = loginDto;
        const { accessToken, refreshToken } = await this.authService.validateUser(email, password);
        return { statusCode: HttpStatus.OK, message: '로그인 성공', accessToken, refreshToken };
    }
}
