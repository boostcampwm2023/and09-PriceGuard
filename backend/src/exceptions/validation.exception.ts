import { BadRequestException, HttpStatus } from '@nestjs/common';

export class ValidationException extends BadRequestException {
    constructor(message: string) {
        super({ statusCode: HttpStatus.BAD_REQUEST, message });
    }

    getMessage() {
        return this.message;
    }
}
