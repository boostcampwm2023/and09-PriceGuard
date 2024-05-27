import * as randomUseragent from 'random-useragent';

export function createRandomNumber(min: number, max: number) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function getSecondsUntilMidnight() {
    const now = new Date();
    const midnight = new Date(now);
    midnight.setHours(24, 0, 0, 0);
    const secondsUntilMidnight = Math.floor((midnight.getTime() - now.getTime()) / 1000);
    return secondsUntilMidnight;
}

function isVersionInRange(version: string) {
    const numericVersion = parseFloat(version);
    return (numericVersion >= 20 && numericVersion < 29) || (numericVersion >= 535 && numericVersion < 538);
}

function isOsNameValid(osName: string) {
    return osName === 'Windows' || osName === 'Mac OS';
}

export function getRandomUserAgent() {
    const userAgent = randomUseragent.getRandom(function (ua) {
        return (
            ua.userAgent.includes('AppleWebKit') &&
            ua.browserName === 'Chrome' &&
            isOsNameValid(ua.osName) &&
            isVersionInRange(ua.engineVersion)
        );
    });
    return userAgent;
}
