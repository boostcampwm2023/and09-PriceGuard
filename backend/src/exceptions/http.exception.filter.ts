import { ArgumentsHost, Catch, ExceptionFilter, HttpException, HttpStatus, Inject } from '@nestjs/common';
import { Response } from 'express';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';

@Catch(HttpException)
export class HttpExceptionFilter implements ExceptionFilter {
    constructor(@Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger) {}
    catch(exception: HttpException, host: ArgumentsHost) {
        const ctx = host.switchToHttp();
        const response = ctx.getResponse<Response>();
        this.logger.error(exception.message);
        if (exception.getStatus() === HttpStatus.INTERNAL_SERVER_ERROR) {
            exception.message = 'Internal server error';
        }
        response.status(exception.getStatus()).json({
            statusCode: exception.getStatus(),
            message: exception.message,
        });
    }
}
