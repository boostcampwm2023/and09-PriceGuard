import { Controller, Get, Param, Res, UseFilters } from '@nestjs/common';
import { ImagesService } from './images.service';
import { HttpExceptionFilter } from 'src/exceptions/http.exception.filter';
import * as path from 'path';
import { Response } from 'express';

@Controller('images')
@UseFilters(HttpExceptionFilter)
export class ImagesController {
    constructor(private readonly imagesService: ImagesService) {}

    @Get('/:imageName')
    async getImage(@Param('imageName') imageName: string, @Res() res: Response) {
        const image = await this.imagesService.getImage(imageName);
        res.set('Content-Type', CONTENT_TYPE[path.extname(imageName)]).send(image);
    }
}

const CONTENT_TYPE: Record<string, string> = {
    '.ico': 'image/x-icon',
    '.png': 'image/png',
    '.jpg': 'image/jpg',
};
