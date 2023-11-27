import { Inject, Injectable, NestMiddleware } from '@nestjs/common';
import { NextFunction, Request, Response } from 'express';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';

@Injectable()
export class LoggerMiddleware implements NestMiddleware {
    constructor(@Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger) {}
    use(request: Request, response: Response, next: NextFunction) {
        const { ip, method, originalUrl } = request;
        const userAgent = request.get('user-agent');
        request.on('close', () => {
            this.logger.info(`${method} ${originalUrl} - ${userAgent} ${ip}`);
        });
        next();
    }
}
