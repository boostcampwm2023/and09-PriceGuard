import { Module, forwardRef } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersController } from './user.controller';
import { UsersService } from './user.service';
import { User } from './user.entity';
import { UsersRepository } from './user.repository';
import { APP_FILTER } from '@nestjs/core';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { AuthModule } from 'src/auth/auth.module';

@Module({
    imports: [TypeOrmModule.forFeature([User]), forwardRef(() => AuthModule)],
    controllers: [UsersController],
    providers: [
        {
            provide: APP_FILTER,
            useClass: UserExceptionFilter,
        },
        UsersService,
        UsersRepository,
    ],
    exports: [UsersService],
})
export class UsersModule {}
