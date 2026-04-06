package eviction;

import java.util.HashMap;
import java.util.Map;

// LRU (Least Recently Used) eviction policy.
//
// Data structure: doubly-linked list + hashmap.
//   - head.next  = LRU (eviction candidate)
//   - tail.prev  = MRU (most recently used)
//   - All operations are O(1).
//
// On every access the accessed node is moved to the tail (MRU end).
// On evict() the node at the head (LRU end) is removed and its key returned.
public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    private static class Node<K> {
        K key;
        Node<K> prev, next;
        Node(K key) { this.key = key; }
    }

    private final Map<K, Node<K>> map  = new HashMap<>();
    private final Node<K>         head = new Node<>(null); // dummy — LRU side
    private final Node<K>         tail = new Node<>(null); // dummy — MRU side

    public LRUEvictionPolicy() {
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void keyAccessed(K key) {
        if (map.containsKey(key)) {
            unlink(map.get(key));       // detach from current position
        }
        Node<K> node = new Node<>(key);
        map.put(key, node);
        linkBeforeTail(node);           // place at MRU end
    }

    @Override
    public K evict() {
        if (head.next == tail) throw new RuntimeException("LRU eviction: cache is empty");
        Node<K> lru = head.next;        // least recently used
        unlink(lru);
        map.remove(lru.key);
        return lru.key;
    }

    // ── Linked-list helpers ──────────────────────────────────────────────────

    private void unlink(Node<K> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void linkBeforeTail(Node<K> node) {
        node.prev       = tail.prev;
        node.next       = tail;
        tail.prev.next  = node;
        tail.prev       = node;
    }
}
