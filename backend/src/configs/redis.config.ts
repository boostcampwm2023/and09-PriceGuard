import { RedisModuleOptions } from '@songkeys/nestjs-redis';
import { REDIS_IPV6, REDIS_URL } from 'src/constants';

export const RedisConfig: RedisModuleOptions = {
    config: {
        url: REDIS_URL,
        family: REDIS_IPV6,
    },
};
