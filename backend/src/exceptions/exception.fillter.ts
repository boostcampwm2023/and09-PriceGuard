import { Catch, ExceptionFilter, ArgumentsHost, HttpException, HttpStatus, Inject } from '@nestjs/common';
import { Response } from 'express';
import { ValidationException } from './validation.exception';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';

@Catch(HttpException)
export class UserExceptionFilter implements ExceptionFilter {
    constructor(@Inject(WINSTON_MODULE_PROVIDER) private readonly logger: Logger) {}
    catch(exception: HttpException, host: ArgumentsHost) {
        const ctx = host.switchToHttp();
        const response = ctx.getResponse<Response>();
        this.logger.error(exception.message);
        if (exception.message.includes('Duplicate entry')) {
            this.setResponse(response, HttpStatus.CONFLICT, '이메일 중복');
            return;
        }
        if (exception instanceof ValidationException) {
            this.setResponse(response, exception.getStatus(), exception.getMessage());
            return;
        }
        this.setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, '서버 내부 에러');
    }

    private setResponse(response: Response, statusCode: number, msg: string) {
        response.status(statusCode).json({
            statusCode: statusCode,
            message: msg,
        });
    }
}
