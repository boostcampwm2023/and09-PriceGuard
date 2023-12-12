import { ProductRankCacheDto } from 'src/dto/product.rank.cache.dto';
import { CacheNode } from './cache.node';

export class ProductRankCache {
    maxSize: number;
    count: number;
    head: CacheNode<ProductRankCacheDto>;
    tail: CacheNode<ProductRankCacheDto>;
    hashMap = new Map<string, CacheNode<ProductRankCacheDto>>();
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
            this.remove(lowestNode);
        }
    }

    private add(node: CacheNode<ProductRankCacheDto>) {
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
        this.hashMap.set(node.key, node);
        this.count++;
    }

    private getLowestNode() {
        let node = this.tail.prev;
        while (node.value.userCount > node.prev.value.userCount) {
            node = node.prev;
        }
        return node;
    }

    delete(key: string) {
        const node = this.hashMap.get(key);
        if (node) {
            this.remove(node);
        }
    }

    private remove(node: CacheNode<ProductRankCacheDto>) {
        const { prev, next } = node;
        prev.next = next;
        next.prev = prev;
        this.hashMap.delete(node.key);
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
        const node = this.hashMap.get(product.id);
        if (node) {
            this.remove(node);
        }
        if (newProduct) {
            this.put(newProduct.id, newProduct);
            return;
        }
        this.put(product.id, product);
    }

    get(key: string): ProductRankCacheDto | null {
        const node = this.hashMap.get(key);
        return node ? node.value : null;
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
