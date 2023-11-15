import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersController } from './user.controller';
import { UsersService } from './user.service';
import { User } from './user.entity';
import { UsersRepository } from './user.repository';
import { APP_FILTER } from '@nestjs/core';
import { DuplicateEmailExceptionFilter } from 'src/exceptions/exception.fillter';

@Module({
    imports: [TypeOrmModule.forFeature([User])],
    controllers: [UsersController],
    providers: [
        {
            provide: APP_FILTER,
            useClass: DuplicateEmailExceptionFilter,
        },
        UsersService,
        UsersRepository,
    ],
})
export class UsersModule {}
