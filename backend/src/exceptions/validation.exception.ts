import { BadRequestException, HttpStatus } from '@nestjs/common';

export class ValidationException extends BadRequestException {
    constructor(message: Record<string, string>) {
        super({ statusCode: HttpStatus.BAD_REQUEST, message: message});
    }
}