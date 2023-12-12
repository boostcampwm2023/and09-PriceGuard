export class CacheNode<T> {
    key: string;
    value: T;
    prev: CacheNode<T>;
    next: CacheNode<T>;

    constructor(key: string, value: T) {
        this.key = key;
        this.value = value;
    }
}
