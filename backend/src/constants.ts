import { config } from 'dotenv';

if (process.env.NODE_ENV === 'dev') {
    config();
}

export const DB_HOST = process.env.DB_HOST;
export const DB_PORT = parseInt(process.env.DB_PORT || '3306');
export const DB_USER = process.env.DB_USER;
export const DB_PASSWORD = process.env.DB_PASSWORD;
export const DB_NAME = process.env.DB_NAME;
export const NODE_ENV = process.env.NODE_ENV;
export const ACCESS_TOKEN_SECRETS = process.env.ACCESS_TOKEN_SECRETS;
export const REFRESH_TOKEN_SECRETS = process.env.REFRESH_TOKEN_SECRETS;
export const OPEN_API_KEY_11ST = process.env.OPEN_API_KEY_11ST as string;
export const BASE_URL_11ST = process.env.BASE_URL_11ST as string;
export const MAX_TRACKING_RANK = parseInt(process.env.MAX_TRACKING_RANK || '50');
