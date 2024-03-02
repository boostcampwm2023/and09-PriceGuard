import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { readFileSync } from 'fs';
import * as path from 'path';

@Injectable()
export class ImagesService {
    constructor() {}

    async getImage(imageName: string) {
        try {
            const image = readFileSync(path.join('./src/images', imageName));
            return image;
        } catch (err) {
            throw new HttpException('Not Found Images', HttpStatus.NOT_FOUND);
        }
    }
}
