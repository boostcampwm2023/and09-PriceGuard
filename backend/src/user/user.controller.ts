import { Body, Controller, Post, HttpStatus } from '@nestjs/common';
import { UsersService } from './user.service';
import { UserDto } from './dto/user.dto';

@Controller('user')
export class UsersController {
    constructor(private userService: UsersService) {}

    @Post('register')
    async registerUser(@Body() userDto: UserDto): Promise<{ statusCode: number, message: string }> {
        await this.userService.registerUser(userDto);
        return { statusCode: HttpStatus.OK, message: '회원가입 성공' };
    }
}
