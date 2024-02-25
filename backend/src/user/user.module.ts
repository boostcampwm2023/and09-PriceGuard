import { Module, forwardRef } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersController } from './user.controller';
import { UsersService } from './user.service';
import { User } from '../entities/user.entity';
import { UsersRepository } from './user.repository';
import { APP_FILTER } from '@nestjs/core';
import { UserExceptionFilter } from 'src/exceptions/exception.fillter';
import { AuthModule } from 'src/auth/auth.module';
import { ProductModule } from 'src/product/product.module';
import { MailService } from 'src/mail/mail.service';

@Module({
    imports: [TypeOrmModule.forFeature([User]), forwardRef(() => AuthModule), ProductModule],
    controllers: [UsersController],
    providers: [
        {
            provide: APP_FILTER,
            useClass: UserExceptionFilter,
        },
        UsersService,
        UsersRepository,
        MailService,
    ],
    exports: [UsersService],
})
export class UsersModule {}
