import { RedisModuleOptions } from '@songkeys/nestjs-redis';
import { REDIS_HOST, REDIS_PASSWORD, REDIS_PORT } from 'src/constants';

export const RedisConfig: RedisModuleOptions = {
    config: {
        host: REDIS_HOST,
        port: REDIS_PORT,
        password: REDIS_PASSWORD,
    },
};
