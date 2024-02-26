import { MailerOptions } from '@nestjs-modules/mailer';
import { MAILER_PW, MAILER_USER } from 'src/constants';

export const MailerConfig: MailerOptions = {
    transport: {
        host: 'smtp.gmail.com',
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
