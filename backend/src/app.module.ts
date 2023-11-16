import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { typeORMConfig } from './configs/typeorm.config';
import { UsersModule } from './user/user.module';

@Module({
    imports: [TypeOrmModule.forRoot(typeORMConfig), UsersModule],
})
export class AppModule {}
