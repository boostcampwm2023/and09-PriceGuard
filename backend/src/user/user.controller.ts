import { Body, Controller, Post, HttpStatus, UseFilters, UsePipes, ValidationPipe } from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from './dto/user.dto';
import { DuplicateEmailExceptionFilter } from 'src/exceptions/exception.fillter';

@Controller('user')
@UseFilters(DuplicateEmailExceptionFilter)
export class UsersController {
    constructor(private userService: UsersService) {}

    @Post('register')
    async registerUser(@Body() userDto: UserDto): Promise<{ statusCode: number, message: string }> {
        await this.userService.registerUser(userDto);
        return { statusCode: HttpStatus.OK, message: '회원가입 성공' };
    }
}
