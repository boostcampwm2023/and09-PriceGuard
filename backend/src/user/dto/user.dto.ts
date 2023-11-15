import { IsEmail, IsString } from 'class-validator';

export class UserDto {
    @IsEmail()
    email: string;

    @IsString()
    userName: string;

    @IsString()
    password: string;
}
