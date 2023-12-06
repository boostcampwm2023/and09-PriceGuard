import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class FirebaseTokenDto {
    @ApiProperty({
        example: 'firebase cloud messaging registeration token',
        description: 'FCM 등록 토큰',
        required: true,
    })
    @IsString()
    token: string;
}
