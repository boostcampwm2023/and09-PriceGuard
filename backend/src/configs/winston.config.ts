import { utilities } from 'nest-winston';
import * as winstonDaily from 'winston-daily-rotate-file';
import * as winston from 'winston';
import { NODE_ENV } from 'src/constants';

const logDir = `${process.cwd()}/logs`;
const logDailyOptions = {
    level: 'info',
    datePattern: 'YYYY-MM-DD',
    dirname: logDir,
    filename: `%DATE%.log`,
    maxFiles: 30,
    zippedArchive: true,
    handleExceptions: true,
};

const logFormat = winston.format.printf(({ level, message, label, timestamp }) => {
    return `${timestamp} [${label}] ${level}: ${message}`;
});

export const winstonConfig = {
    format: winston.format.combine(
        winston.format.colorize(),
        winston.format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
        winston.format.label({
            label: 'LOGGER',
        }),
        logFormat,
    ),
    transports: [
        new winston.transports.Console({
            level: NODE_ENV === 'prod' ? 'http' : 'silly',
            format:
                NODE_ENV === 'prod'
                    ? winston.format.simple()
                    : winston.format.combine(
                          winston.format.timestamp(),
                          utilities.format.nestLike('PriceGuard', {
                              prettyPrint: true,
                          }),
                      ),
        }),
        new winstonDaily(logDailyOptions),
    ],
    exceptionHandlers: [
        new winstonDaily({
            level: 'error',
            datePattern: 'YYYY-MM-DD',
            dirname: logDir,
            filename: `%DATE%.exception.log`,
            maxFiles: 30,
            zippedArchive: true,
        }),
    ],
};
