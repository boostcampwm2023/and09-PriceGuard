import { TypeOrmModuleOptions } from '@nestjs/typeorm';
import { DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME, NODE_ENV } from 'src/constants';

export const TypeOrmConfig: TypeOrmModuleOptions = {
    type: 'mysql',
    host: DB_HOST,
    port: DB_PORT,
    username: DB_USER,
    password: DB_PASSWORD,
    database: DB_NAME,
    entities: ['dist/**/*.entity{.ts,.js}'],
    synchronize: NODE_ENV === 'dev',
};
