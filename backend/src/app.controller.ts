import { Controller, Get, HttpStatus } from '@nestjs/common';
import { AppService } from './app.service';
import { ApiOkResponse, ApiOperation, ApiProperty, ApiTags } from '@nestjs/swagger';

class PingType {
    @ApiProperty({
        example: HttpStatus.OK,
        description: 'Http 상태 코드',
    })
    statusCode: number;
    @ApiProperty({
        example: 'Hello World!',
        description: 'ping 결과',
    })
    message: string;
}

@ApiTags('기본 API')
@Controller('app')
export class AppController {
    constructor(private readonly appService: AppService) {}

    @ApiOperation({ summary: 'ping API', description: 'ping API' })
    @ApiOkResponse({ type: PingType, description: 'ping 성공' })
    @Get('ping')
    getPing(): PingType {
        return { statusCode: HttpStatus.OK, message: this.appService.getPing() };
    }
}
