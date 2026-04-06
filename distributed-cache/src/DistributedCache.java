import db.Database;
import eviction.LRUEvictionPolicy;
import strategy.DistributionStrategy;

import java.util.ArrayList;
import java.util.List;

// Facade that coordinates cache nodes, distribution strategy, and the database.
//
// Distribution:
//   The DistributionStrategy maps a key → node index.
//   Default: ModuloDistributionStrategy (hash % nodes).
//   Future:  swap in ConsistentHashingStrategy with zero code changes here.
//
// Cache miss handling (get):
//   1. Determine the responsible node via the distribution strategy.
//   2. If the node has the key → return it (cache hit).
//   3. Otherwise → fetch from the database (cache miss),
//      store the result in the node, then return it.
//
// Write policy (put):
//   Write-through: value is written to the cache node AND the database
//   atomically from the caller's perspective.
//   Assumption: no separate async write-back is needed for this exercise.
//
// Eviction:
//   Each CacheNode owns its EvictionPolicy instance.
//   On construction, every node gets a fresh LRUEvictionPolicy.
//   The policy is fully isolated per node — no cross-node eviction.
public class DistributedCache {

    private final List<CacheNode>        nodes;
    private final DistributionStrategy   strategy;
    private final Database               database;

    public DistributedCache(int numberOfNodes, int nodeCapacity,
                            DistributionStrategy strategy, Database database) {
        this.strategy  = strategy;
        this.database  = database;
        this.nodes     = new ArrayList<>();

        for (int i = 0; i < numberOfNodes; i++) {
            // Each node gets its own independent LRU policy instance.
            nodes.add(new CacheNode(i, nodeCapacity, new LRUEvictionPolicy<>()));
        }
        System.out.println("[DistributedCache] Initialized with " + numberOfNodes
                + " nodes, capacity=" + nodeCapacity + " each.");
    }

    // ── Public API ───────────────────────────────────────────────────────────

    public String get(String key) {
        CacheNode node      = nodeFor(key);
        String    nodeLabel = "Node-" + node.getNodeId();

        if (node.contains(key)) {
            System.out.println("[HIT]  key=" + key + " → " + nodeLabel);
            return node.get(key);
        }

        // Cache miss — load from DB, populate cache, return value.
        System.out.println("[MISS] key=" + key + " → " + nodeLabel + " — fetching from DB");
        String value = database.get(key);
        if (value != null) {
            node.put(key, value);
        }
        return value;
    }

    public void put(String key, String value) {
        CacheNode node = nodeFor(key);
        node.put(key, value);
        database.put(key, value);           // write-through
        System.out.println("[PUT]  key=" + key + " → Node-" + node.getNodeId());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private CacheNode nodeFor(String key) {
        int index = strategy.getNodeIndex(key, nodes.size());
        return nodes.get(index);
    }
}
