import { TrackingProduct } from 'src/entities/trackingProduct.entity';
import { CacheNode } from './cache.node';

export class TrackingProductCache {
    private maxSize: number;
    private count: number;
    head: CacheNode<TrackingProduct[]>;
    tail: CacheNode<TrackingProduct[]>;
    hashMap = new Map<string, CacheNode<TrackingProduct[]>>();
    constructor(size: number) {
        this.maxSize = size;
        this.head = new CacheNode('head', [new TrackingProduct()]);
        this.tail = new CacheNode('tail', [new TrackingProduct()]);
        this.head.next = this.tail;
        this.tail.prev = this.head;
        this.count = 0;
    }

    put(key: string, value: TrackingProduct[]) {
        this.delete(key);
        const node = new CacheNode(key, value);
        this.add(node);
        this.hashMap.set(key, node);
        if (this.count > this.maxSize) {
            const oldestNode = this.head.next;
            this.remove(oldestNode);
        }
    }

    private add(node: CacheNode<TrackingProduct[]>) {
        const prev = this.tail.prev;
        prev.next = node;
        node.next = this.tail;
        node.prev = prev;
        this.tail.prev = node;
        this.count++;
    }

    delete(key: string) {
        const node = this.hashMap.get(key);
        if (node) {
            this.remove(node);
            this.hashMap.delete(key);
        }
    }

    private remove(node: CacheNode<TrackingProduct[]>) {
        const { prev, next } = node;
        prev.next = next;
        next.prev = prev;
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

    get(key: string): TrackingProduct[] | null {
        const node = this.hashMap.get(key);
        if (node) {
            this.remove(node);
            this.add(node);
            return node.value;
        }
        return null;
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

    addValue(key: string, value: TrackingProduct) {
        const node = this.hashMap.get(key);
        if (node) {
            node.value.push(value);
            this.remove(node);
            this.add(node);
        }
    }

    deleteValue(key: string, value: TrackingProduct) {
        const node = this.hashMap.get(key);
        if (node) {
            node.value = node.value.filter((product) => {
                return product.productId !== value.productId;
            });
            this.remove(node);
            this.add(node);
        }
    }

    updateValue(key: string, value: TrackingProduct) {
        const node = this.hashMap.get(key);
        if (node) {
            node.value.forEach((product, idx) => {
                if (product.productId === value.productId) {
                    node.value[idx] = value;
                    return false;
                }
            });
            this.remove(node);
            this.add(node);
        }
    }

    size() {
        return this.count;
    }
    clear() {
        this.count = 0;
        this.head.next = this.tail;
        this.tail.prev = this.head;
        this.hashMap = new Map<string, CacheNode<TrackingProduct[]>>();
    }
}
