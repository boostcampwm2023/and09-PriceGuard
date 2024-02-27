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
    Get,
} from '@nestjs/common';
import { UsersService } from './user.service';
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
    ApiNotFoundResponse,
    ApiOkResponse,
    ApiOperation,
    ApiTags,
    ApiTooManyRequestsResponse,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import {
    BadRequestError,
    ChangePasswordSuccess,
    CheckEmailVerificatedSuccess,
    DupEmailError,
    EmailNotFound,
    FirebaseTokenSuccess,
    InvalidVerificationCode,
    LoginFailError,
    LoginSuccess,
    RegisterSuccess,
    RemoveFailError,
    RemoveSuccess,
    SendVerificationEmailError,
    SendVerificationEmailSuccess,
    TooManySendEmailError,
} from 'src/dto/user.swagger.dto';
import { FirebaseTokenDto } from 'src/dto/firebase.token.dto';
import { RequestError, UnauthorizedRequest } from 'src/dto/product.swagger.dto';
import { AuthGuard } from '@nestjs/passport';
import { User } from 'src/entities/user.entity';
import { ExpiredTokenError, InvalidTokenError } from 'src/dto/auth.swagger.dto';
import { UserEmailDto } from 'src/dto/user.email.dto';
import { UserRegisterDto } from 'src/dto/user.register.dto';
import { UserPasswordDto } from 'src/dto/user.password.dto';

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
    @ApiBody({ type: UserRegisterDto })
    @ApiOkResponse({ type: RegisterSuccess, description: '회원가입 성공' })
    @ApiBadRequestResponse({ type: BadRequestError, description: '유효하지 않은 입력 값' })
    @ApiConflictResponse({ type: DupEmailError, description: '이메일 중복' })
    @ApiUnauthorizedResponse({ type: InvalidVerificationCode, description: '인증 코드 불일치' })
    @ApiNotFoundResponse({ type: EmailNotFound, description: '해당 이메일로 발급받은 인증 코드가 없거나 만료됨' })
    @Post('register')
    async registerUser(@Body() userRegisterDto: UserRegisterDto): Promise<RegisterSuccess> {
        const user = await this.userService.registerUser(userRegisterDto);
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

    @ApiOperation({ summary: '회원탈퇴 API', description: '서버에게 회원탈퇴 요청을 보낸다.' })
    @ApiBody({ type: LoginDto })
    @ApiOkResponse({ type: RemoveSuccess, description: '회원탈퇴 성공' })
    @ApiBadRequestResponse({ type: RemoveFailError, description: '회원탈퇴 실패' })
    @Post('remove')
    async deleteUser(@Body() loginDto: LoginDto): Promise<RemoveSuccess> {
        const { email, password } = loginDto;
        await this.userService.removeUser(email, password);
        return { statusCode: HttpStatus.OK, message: '회원탈퇴 성공' };
    }

    @ApiOperation({
        summary: '회원가입을 위한 이메일 인증 코드 발송 요청 API',
        description: '회원가입을 위해 사용자 이메일로 인증 코드를 발송한다.',
    })
    @ApiBody({ type: UserEmailDto })
    @ApiOkResponse({ type: SendVerificationEmailSuccess, description: '이메일 인증 코드 발송 성공' })
    @ApiBadRequestResponse({ type: SendVerificationEmailError, description: '이메일 인증 코드 발송 실패' })
    @ApiTooManyRequestsResponse({ type: TooManySendEmailError, description: '이메일 발송 하루 최대 횟수 초과' })
    @ApiConflictResponse({ type: DupEmailError, description: '이메일 중복' })
    @Post('email/register-verification')
    async sendRegisterVeryficationEmail(@Body() userEmailDto: UserEmailDto) {
        await this.userService.sendRegisterVerificationEmail(userEmailDto.email);
        return { statusCode: HttpStatus.OK, message: '이메일 전송 성공' };
    }

    @ApiOperation({
        summary: '사용자 이메일 인증 여부 확인 API',
        description: '사용자 이메일이 인증되어 있는지 알려준다.',
    })
    @ApiHeader({
        name: 'Authorization Bearer Token',
        description: 'accessToken',
    })
    @ApiOkResponse({ type: CheckEmailVerificatedSuccess, description: '이메일 인증 코드 발송 성공' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @ApiNotFoundResponse({ type: EmailNotFound, description: '해당 이메일의 사용자를 찾을 수 없음' })
    @ApiUnauthorizedResponse({ type: UnauthorizedRequest, description: '승인되지 않은 요청' })
    @ApiGoneResponse({ type: ExpiredTokenError, description: 'accessToken 만료' })
    @UseGuards(AuthGuard('access'))
    @Get('email/is-verified')
    async checkEmailVarifacted(@Req() req: Request & { user: User }) {
        const verified = await this.userService.checkEmailVarifacted(req.user.email);
        return { statusCode: HttpStatus.OK, verified, message: '사용자 이메일 인증 여부 조회 성공' };
    }

    @ApiOperation({
        summary: '기존 사용자 이메일 인증 코드 발송 요청 API',
        description: '기존 사용자 이메일로 인증 코드를 발송한다.',
    })
    @ApiBody({ type: UserEmailDto })
    @ApiOkResponse({ type: SendVerificationEmailSuccess, description: '이메일 인증 코드 발송 성공' })
    @ApiBadRequestResponse({ type: SendVerificationEmailError, description: '이메일 인증 코드 발송 실패' })
    @ApiTooManyRequestsResponse({ type: TooManySendEmailError, description: '이메일 발송 하루 최대 횟수 초과' })
    @ApiNotFoundResponse({ type: EmailNotFound, description: '해당 이메일을 찾을 수 없음' })
    @Post('email/verification')
    async sendVeryficationEmail(@Body() userEmailDto: UserEmailDto) {
        await this.userService.sendVerificationEmail(userEmailDto.email);
        return { statusCode: HttpStatus.OK, message: '이메일 전송 성공' };
    }

    @ApiOperation({ summary: '사용자 비밀번호 변경 API', description: '사용자가 비밀번호를 변경한다.' })
    @ApiHeader({
        name: 'Authorization Bearer Token',
        description: 'verifyToken',
    })
    @ApiOkResponse({ type: ChangePasswordSuccess, description: '비밀번호 변경 성공' })
    @ApiGoneResponse({ type: ExpiredTokenError, description: 'verifyToken 만료' })
    @ApiUnauthorizedResponse({ type: InvalidTokenError, description: '유효하지 않은 verifyToken' })
    @ApiNotFoundResponse({ type: EmailNotFound, description: '해당 이메일을 찾을 수 없음' })
    @ApiBadRequestResponse({ type: RequestError, description: '잘못된 요청입니다.' })
    @Post('/password')
    @UseGuards(AuthGuard('verify'))
    async changePassword(@Req() req: Request & { user: User }, @Body() userPasswordDto: UserPasswordDto) {
        await this.userService.changePassword(req.user.email, userPasswordDto.password);
        return { statusCode: HttpStatus.OK, message: '비밀번호 변경 성공' };
    }
}
