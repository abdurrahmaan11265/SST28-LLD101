import db.InMemoryDatabase;
import strategy.ModuloDistributionStrategy;

// Demonstrates:
//   1. Cache miss  — key fetched from DB and stored in cache
//   2. Cache hit   — second access served from cache
//   3. put()       — write-through to both cache and DB
//   4. LRU eviction — capacity exceeded, LRU key is dropped
public class Main {

    public static void main(String[] args) {

        InMemoryDatabase      db       = new InMemoryDatabase();
        ModuloDistributionStrategy strategy = new ModuloDistributionStrategy();

        // 3 nodes, each holding at most 2 keys — keeps eviction easy to trigger.
        DistributedCache cache = new DistributedCache(3, 2, strategy, db);

        separator("1. Cache MISS — loads from DB then caches");
        System.out.println("Value: " + cache.get("user:1"));     // miss → DB
        System.out.println("Value: " + cache.get("product:1"));  // miss → DB

        separator("2. Cache HIT — served from cache");
        System.out.println("Value: " + cache.get("user:1"));     // hit
        System.out.println("Value: " + cache.get("product:1"));  // hit

        separator("3. put() — write-through to cache + DB");
        cache.put("user:4", "Diana");
        System.out.println("Value: " + cache.get("user:4"));     // hit (just written)

        separator("4. LRU eviction — fill Node-2 beyond capacity");
        // Node-2 currently holds: user:1 (accessed in step 2), user:4 (accessed in step 3).
        // user:1 is LRU (older access), user:4 is MRU.
        // order:3 and order:6 both hash to Node-2 (verified: Math.abs(hash) % 3 == 2).
        // Adding order:3 → node is full (user:1, user:4, order:3 would be 3 > capacity 2).
        // So user:1 (LRU) is evicted before order:3 is inserted.
        System.out.println("-- adding order:3 to Node-2 (full at capacity 2) --");
        cache.put("order:3", "Widget");   // triggers eviction of LRU key on Node-2

        System.out.println("-- adding order:6 to Node-2 --");
        cache.put("order:6", "Gadget");   // triggers eviction again

        separator("5. Accessing a key not in DB returns null");
        System.out.println("Value: " + cache.get("nonexistent"));
    }

    private static void separator(String title) {
        System.out.println("\n──────────────────────────────────────────");
        System.out.println("  " + title);
        System.out.println("──────────────────────────────────────────");
    }
}
