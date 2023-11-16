import { TypeOrmModuleOptions } from '@nestjs/typeorm';
import { config } from 'dotenv';

if (process.env.NODE_ENV === 'dev') {
    config();
}

export const typeORMConfig: TypeOrmModuleOptions = {
    type: 'mysql',
    host: process.env.DB_HOST,
    port: parseInt(process.env.DB_PORT || '3306'),
    username: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    entities: ['dist/**/*.entity{.ts,.js}'],
    synchronize: process.env.NODE_ENV === 'dev',
};
