class CacheNode {
    key: string;
    value: any;
    prev: CacheNode;
    next: CacheNode;
    constructor(key: string, value: any) {
        this.key = key;
        this.value = value;
    }
}

export class RankCache {
    maxSize: number;
    count: number;
    head: CacheNode;
    tail: CacheNode;
    constructor(size: number) {
        this.maxSize = size;
        this.head = new CacheNode('head', 'head');
        this.tail = new CacheNode('tail', 'tail');
    }

    put(key: string, value: any) {
        if (this.tail.prev.value >= value) {
            return;
        }
        const node = new CacheNode(key, value);
        this.add(node);
        if (this.count > this.maxSize) {
            const lowestNode = this.getlowestNode();
            this.delete(lowestNode);
        }
    }

    private add(node: CacheNode) {
        const prev = this.tail.prev;
        prev.next = node;
        this.tail.prev = node;
        node.prev = prev;
        node.next = this.tail;
        this.count++;
    }

    private getlowestNode() {
        let node = this.tail.prev;
        while (node.value > node.prev.value) {
            node = node.prev;
        }
        return node;
    }

    delete(node: CacheNode) {
        const prev = node.prev;
        const next = node.next;
        prev.next = next;
        next.prev = prev;
    }
}
