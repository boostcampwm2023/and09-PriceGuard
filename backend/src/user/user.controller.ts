import { Body, Controller, Post, HttpStatus, UseFilters, UsePipes } from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from './dto/user.dto';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { UserValidationPipe } from 'src/exceptions/validation.user.pipe';

@Controller('user')
@UseFilters(UserExceptionFilter)
export class UsersController {
    constructor(private userService: UsersService) {}

    @Post('register')
    @UsePipes(new UserValidationPipe())
    async registerUser(@Body() userDto: UserDto): Promise<{ statusCode: number; message: string }> {
        await this.userService.registerUser(userDto);
        return { statusCode: HttpStatus.OK, message: '회원가입 성공' };
    }
}
