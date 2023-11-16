import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TypeORMConfig } from './configs/typeorm.config';
import { UsersModule } from './user/user.module';

@Module({
    imports: [TypeOrmModule.forRoot(TypeORMConfig), UsersModule],
})
export class AppModule {}
