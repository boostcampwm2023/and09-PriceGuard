import { Catch, ExceptionFilter, ArgumentsHost, HttpException, HttpStatus } from '@nestjs/common';
import { Response } from 'express';
import { ValidationException } from './validation.exception';

@Catch(HttpException)
export class UserExceptionFilter implements ExceptionFilter {
    catch(exception: HttpException, host: ArgumentsHost) {
        const ctx = host.switchToHttp();
        const response = ctx.getResponse<Response>();

        if (exception.message.includes('Duplicate entry')) {
            this.setResposne(response, HttpStatus.CONFLICT, '이메일 중복');
            return;
        }
        if (exception instanceof ValidationException) {
            this.setResposne(response, exception.getStatus(), exception.getMessage());
            return;
        }
        this.setResposne(response, HttpStatus.INTERNAL_SERVER_ERROR, '서버 내부 에러');
    }

    private setResposne(response: Response, statusCode: number, msg: string) {
        response.status(statusCode).json({
            statusCode: statusCode,
            message: msg,
        });
    }
}
