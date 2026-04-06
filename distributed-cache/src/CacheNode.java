import eviction.EvictionPolicy;
import java.util.HashMap;
import java.util.Map;

// A single logical cache node with a fixed capacity.
//
// Eviction policy is injected — CacheNode has no knowledge of LRU/LFU/MRU.
// When the node is full and a new (distinct) key is inserted, the policy
// decides which existing key to drop before the new one is stored.
public class CacheNode {

    private final int                        capacity;
    private final Map<String, String>        store;
    private final EvictionPolicy<String>     evictionPolicy;
    private final int                        nodeId;

    public CacheNode(int nodeId, int capacity, EvictionPolicy<String> evictionPolicy) {
        this.nodeId         = nodeId;
        this.capacity       = capacity;
        this.store          = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    // Returns the value for key, or null if not present.
    // Marks the key as accessed (affects eviction order).
    public String get(String key) {
        if (!store.containsKey(key)) return null;
        evictionPolicy.keyAccessed(key);
        return store.get(key);
    }

    // Stores key→value.
    // If the node is at capacity and the key is new, one entry is evicted first.
    public void put(String key, String value) {
        if (!store.containsKey(key) && store.size() >= capacity) {
            String evicted = evictionPolicy.evict();
            store.remove(evicted);
            System.out.println("[Node-" + nodeId + "] Evicted key: " + evicted);
        }
        store.put(key, value);
        evictionPolicy.keyAccessed(key);
    }

    public boolean contains(String key) { return store.containsKey(key); }
    public int     getNodeId()          { return nodeId; }
    public int     size()               { return store.size(); }
}
