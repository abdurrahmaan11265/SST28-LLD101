package strategy;

// Strategy interface for deciding which cache node owns a given key.
// Pluggable: swap in ConsistentHashing, RendezvousHashing, etc. without
// touching DistributedCache.
public interface DistributionStrategy {

    // Returns the index (0-based) of the node responsible for this key.
    int getNodeIndex(String key, int numberOfNodes);
}
