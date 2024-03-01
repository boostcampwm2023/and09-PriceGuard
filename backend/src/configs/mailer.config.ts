import { MailerOptions } from '@nestjs-modules/mailer';
import { MAILER_HOST, MAILER_PW, MAILER_USER } from 'src/constants';

export const MailerConfig: MailerOptions = {
    transport: {
        host: MAILER_HOST,
        prot: 587,
        secure: false,
        auth: {
            user: MAILER_USER,
            pass: MAILER_PW,
        },
    },
    defaults: {
        from: '"PriceGuard" <no-reply@priceguard.app>',
    },
};
