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
export const INVALIDATED_REFRESHTOKEN = 'invalidate refreshToken';
export const MONGODB_URL = process.env.MONGODB_URL as string;
export const THIRTY_DAYS = 30;
export const NINETY_DAYS = 90;
export const NO_CACHE = 0;
export const REDIS_HOST = process.env.REDIS_HOST;
export const REDIS_PORT = process.env.REDIS_PORT;
export const REDIS_PASSWORD = process.env.REDIS_PASSWORD;
export const TYPE = process.env.TYPE;
export const PROJECT_ID = process.env.PROJECT_ID;
export const PRIVATE_KEY_ID = process.env.PRIVATE_KEY_ID;
export const PRIVATE_KEY = process.env.PRIVATE_KEY as string;
export const CLIENT_EMAIL = process.env.CLIENT_EMAIL;
export const CLIENT_ID = process.env.CLIENT_ID;
export const AUTH_URI = process.env.AUTH_URI;
export const TOKEN_URI = process.env.TOKEN_URI;
export const AUTH_PROVIDER_X509_CERT_URL = process.env.AUTH_PROVIDER_X509_CERT_URL;
export const CLIENT_X509_CERT_URL = process.env.CLIENT_X509_CERT_URL;
export const UNIVERSE_DOMAIN = process.env.UNIVERSE_DOMAIN;
