import { ValidationPipe } from '@nestjs/common';
import { ValidationException } from './validation.exception';

export class UserValidationPipe extends ValidationPipe {
    exceptionFactory = (errors: any[]) => {
        errors.map((error) => {
            return {
                property: error.property,
                constraints: error.constraints,
                message: 'Custom validation error message',
            };
        });
        throw new ValidationException('입력 값이 유효하지 않습니다');
    };
}
