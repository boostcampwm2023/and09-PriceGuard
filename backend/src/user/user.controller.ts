import {
    Body,
    Controller,
    Post,
    HttpStatus,
    UseFilters,
    forwardRef,
    Inject,
    Put,
    UseGuards,
    Req,
} from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from '../dto/user.dto';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { AuthService } from '../auth/auth.service';
import { LoginDto } from '../dto/login.dto';
import {
    ApiBadRequestResponse,
    ApiBearerAuth,
    ApiBody,
    ApiConflictResponse,
    ApiGoneResponse,
    ApiHeader,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import {
    BadRequestError,
    DupEmailError,
    FirebaseTokenSuccess,
    LoginFailError,
    LoginSuccess,
    RegisterSuccess,
} from 'src/dto/user.swagger.dto';
import { FirebaseTokenDto } from 'src/dto/firebase.token.dto';
import { UnauthorizedRequest } from 'src/dto/product.swagger.dto';
import { AuthGuard } from '@nestjs/passport';
import { User } from 'src/entities/user.entity';
import { ExpiredTokenError } from 'src/dto/auth.swagger.dto';

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
    async loginUser(@Body() loginDto: LoginDto): Promise<LoginSuccess> {
        const { email, password } = loginDto;
        const { accessToken, refreshToken } = await this.authService.validateUser(email, password);
        return { statusCode: HttpStatus.OK, message: '로그인 성공', accessToken, refreshToken };
    }
    @ApiBearerAuth()
    @ApiHeader({
        name: 'Authorization',
        description: '사용자 인증을 위한 AccessToken이다. ex) Bearer [token]',
    })
    @ApiOkResponse({ type: FirebaseTokenSuccess, description: '유저 FCM 토큰 생성 or 갱신' })
    @ApiUnauthorizedResponse({ type: UnauthorizedRequest, description: '승인되지 않은 요청' })
    @ApiGoneResponse({ type: ExpiredTokenError, description: 'accessToken 만료' })
    @UseGuards(AuthGuard('access'))
    @Put('firebase/token')
    async registerFirebaseToken(
        @Req() req: Request & { user: User },
        @Body() firebaseTokenDto: FirebaseTokenDto,
    ): Promise<FirebaseTokenSuccess> {
        const { token } = firebaseTokenDto;
        await this.authService.addFirebaseToken(req.user.id, token);
        return { statusCode: HttpStatus.OK, message: 'FCM 토큰 등록 성공' };
    }
}
