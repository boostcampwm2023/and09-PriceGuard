import { IsEmail, IsString } from 'class-validator';

export class UserDto {
    @IsEmail()
    email: string;

    @IsString()
    username: string;

    @IsString()
    password: string;
}
