package eviction;

// Strategy interface for cache eviction.
// Implementations decide which key to remove when the cache is full.
// Pluggable: swap in MRU, LFU, etc. without touching CacheNode.
public interface EvictionPolicy<K> {

    // Called every time a key is read or written (marks it as recently used).
    void keyAccessed(K key);

    // Returns the key that should be evicted next.
    K evict();
}
