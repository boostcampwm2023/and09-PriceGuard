import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';

class CacheNode {
    key: string;
    value: ProductRankCacheDto;
    prev: CacheNode;
    next: CacheNode;
    constructor(key: string, value: ProductRankCacheDto) {
        this.key = key;
        this.value = value;
    }
}

export class ProductRankCache {
    maxSize: number;
    count: number;
    head: CacheNode;
    tail: CacheNode;
    hashTable = new Map<string, CacheNode>();
    constructor(size: number) {
        this.maxSize = size;
        this.head = new CacheNode('head', new ProductRankCacheDto());
        this.tail = new CacheNode('tail', new ProductRankCacheDto());
        this.head.next = this.tail;
        this.tail.prev = this.head;
        this.count = 0;
    }

    put(key: string, value: ProductRankCacheDto) {
        const node = new CacheNode(key, value);
        this.add(node);
        if (this.count > this.maxSize) {
            const lowestNode = this.getLowestNode();
            this.delete(lowestNode);
        }
    }

    private add(node: CacheNode) {
        let prev = this.tail.prev;
        while (prev.value.userCount <= node.value.userCount) {
            if (prev.value.userCount === node.value.userCount && prev.value.id > node.value.id) {
                break;
            }
            if (prev === this.head) {
                break;
            }
            prev = prev.prev;
        }
        const next = prev.next;
        prev.next = node;
        node.next = next;
        node.prev = prev;
        next.prev = node;
        this.hashTable.set(node.key, node);
        this.count++;
    }

    private getLowestNode() {
        let node = this.tail.prev;
        while (node.value.userCount > node.prev.value.userCount) {
            node = node.prev;
        }
        return node;
    }

    delete(node: CacheNode) {
        const { prev, next } = node;
        prev.next = next;
        next.prev = prev;
        this.hashTable.delete(node.key);
        this.count--;
    }

    findIndex(key: string) {
        let node = this.head.next;
        let idx = 0;
        while (node.key !== key) {
            idx++;
            if (this.count <= idx) {
                idx = -1;
                break;
            }
            node = node.next;
        }
        return idx;
    }

    update(product: ProductRankCacheDto, newProduct?: ProductRankCacheDto) {
        const node = this.hashTable.get(product.id);
        if (node) {
            this.delete(node);
        }
        if (newProduct) {
            this.put(newProduct.id, newProduct);
            return;
        }
        this.put(product.id, product);
    }

    get(key: string): CacheNode | null {
        const node = this.hashTable.get(key);
        return node ? node : null;
    }

    getAll() {
        const nodeList = [];
        let node = this.head.next;
        while (node !== this.tail) {
            nodeList.push(node.value);
            node = node.next;
        }
        return nodeList;
    }
}
