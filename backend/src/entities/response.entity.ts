import { ApiProperty } from '@nestjs/swagger';

export class OkResponse {
    @ApiProperty({
        example: 200,
        description: 'Http Status Code',
        required: true,
    })
    code: number;
    @ApiProperty({
        example: '성공',
        description: '응답 메시지',
        required: true,
    })
    message: string;
}

export class JwtResponse extends OkResponse {
    constructor() {
        super();
    }
    @ApiProperty({
        example: 'access example',
        description: 'Access Token의 만료 기간은 5분이다.',
    })
    accessToken: string;
    @ApiProperty({
        example: 'refresh example',
        description: 'Refresh Token의 만료 기간은 2주이다.',
    })
    refreshToken: string;
}
