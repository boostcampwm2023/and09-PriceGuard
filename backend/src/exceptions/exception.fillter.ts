import { Catch, ExceptionFilter, ArgumentsHost, HttpException, HttpStatus } from '@nestjs/common';
import { Response } from 'express';
import { ValidationException } from './validation.exception';

@Catch(HttpException)
export class DuplicateEmailExceptionFilter implements ExceptionFilter {
    catch(exception: HttpException, host: ArgumentsHost) {
        const ctx = host.switchToHttp();
        const response = ctx.getResponse<Response>();

        if (exception.message.includes('Duplicate entry')) {
            response.status(HttpStatus.CONFLICT).json({
                statusCode: HttpStatus.CONFLICT,
                message: '이메일 중복',
            });
            return;
        }
        if (exception instanceof ValidationException) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR).json({
                statusCode: exception.getStatus(),
                message: exception.getMessage(),
            });
            return;
        }
        response.status(HttpStatus.INTERNAL_SERVER_ERROR).json({
            statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
            message: '서버 내부 에러',
        });
    }
}
