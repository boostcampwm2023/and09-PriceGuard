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
