import { HttpException, HttpStatus, Injectable } from '@nestjs/common';
import { MailerService } from '@nestjs-modules/mailer';
import { createRandomNumber, getSecondsUntilMidnight } from 'src/utils/util';
import { MAX_SENDING_EMAIL, MAX_VERFICATION_CODE, MIN_VERFICATION_CODE, THREE_MIN_TO_SEC } from 'src/constants';
import Redis from 'ioredis';
import { InjectRedis } from '@songkeys/nestjs-redis';

@Injectable()
export class MailService {
    constructor(
        private readonly mailerService: MailerService,
        @InjectRedis() private readonly redis: Redis,
    ) {}

    async sendVerficationCode(email: string) {
        const count = await this.checkMaxCount(email);
        const code = createRandomNumber(MIN_VERFICATION_CODE, MAX_VERFICATION_CODE);
        await this.mailerService.sendMail({
            to: email,
            subject: 'PriceGuard 이메일 인증',
            text: `인증번호는 ${code} 입니다`,
        });
        await this.plusSendingCount(email, count);
        await this.redis.set(`verficationCode:${email}`, code, 'EX', THREE_MIN_TO_SEC);
    }

    async checkMaxCount(email: string) {
        const count = await this.redis.get(`emailCount:${email}`);
        const numCount = count ? parseFloat(count) : 0;
        if (numCount >= MAX_SENDING_EMAIL) {
            throw new HttpException('이메일 발송 하루 최대 횟수 초과', HttpStatus.TOO_MANY_REQUESTS);
        }
        return numCount;
    }

    async plusSendingCount(email: string, count: number) {
        const TTL = getSecondsUntilMidnight();
        await this.redis.set(`emailCount:${email}`, count + 1, 'EX', TTL);
    }
}
