import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TypeOrmConfig } from './configs/typeorm.config';
import { UsersModule } from './user/user.module';
import { AuthModule } from './auth/auth.module';
import { WinstonModule } from 'nest-winston';
import { winstonConfig } from './configs/winston.config';
import { ProductModule } from './product/product.module';

@Module({
    imports: [
        TypeOrmModule.forRoot(TypeOrmConfig),
        WinstonModule.forRoot(winstonConfig),
        UsersModule,
        AuthModule,
        ProductModule,
    ],
})
export class AppModule {}
