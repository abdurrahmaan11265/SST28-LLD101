package strategy;

// Simplest distribution strategy: hash(key) % numberOfNodes.
// Deterministic and uniform for well-spread keys.
//
// Limitation: adding/removing a node reassigns almost every key
//             (consistent hashing solves this — drop-in via the interface).
public class ModuloDistributionStrategy implements DistributionStrategy {

    @Override
    public int getNodeIndex(String key, int numberOfNodes) {
        // Math.abs guards against negative hashCode values.
        return Math.abs(key.hashCode()) % numberOfNodes;
    }
}
