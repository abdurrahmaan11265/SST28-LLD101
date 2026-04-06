# Distributed Cache — LLD

A low-level design implementation of a distributed in-memory cache system supporting pluggable distribution strategies and eviction policies.

---

## Class Diagram

<!-- TODO: Add UML class diagram here -->

---

## Project Structure

```
distributed-cache/src/
├── Main.java                            # Demo / driver
├── DistributedCache.java                # Facade — coordinates nodes, strategy, DB
├── CacheNode.java                       # Single cache node with eviction
├── db/
│   ├── Database.java                    # Interface for backing store
│   └── InMemoryDatabase.java            # Stub implementation
├── eviction/
│   ├── EvictionPolicy.java              # Interface for eviction strategies
│   └── LRUEvictionPolicy.java           # LRU via doubly-linked list + hashmap (O(1))
└── strategy/
    ├── DistributionStrategy.java        # Interface for key routing
    └── ModuloDistributionStrategy.java  # hash(key) % numberOfNodes
```

---

## Design Overview

### How Data is Distributed

Every `get` and `put` call first routes the key to a node using the `DistributionStrategy`:

```
nodeIndex = Math.abs(key.hashCode()) % numberOfNodes   // ModuloDistributionStrategy
```

The strategy is injected into `DistributedCache`, making it easy to swap in consistent hashing or any other approach without changing any other class.

### Cache Miss Handling

```
get(key)
 ├─ node.contains(key) == true  →  HIT  — return value from cache
 └─ node.contains(key) == false →  MISS — fetch from DB, store in node, return value
```

### Write Policy

`put(key, value)` uses **write-through**: the value is written to the cache node and the database in the same call, keeping them consistent.

### Eviction (LRU)

`LRUEvictionPolicy` uses a **doubly-linked list + hashmap**:

| Operation | Complexity |
|---|---|
| `keyAccessed(key)` — move to MRU tail | O(1) |
| `evict()` — remove LRU head | O(1) |

Each `CacheNode` owns its own policy instance. When a new key is inserted into a full node, the LRU key is evicted before the new one is stored.

---

## Extensibility

The design is built around three interfaces, each independently pluggable:

| Concern | Interface | Current Implementation | Easy to swap with |
|---|---|---|---|
| Key routing | `DistributionStrategy` | `ModuloDistributionStrategy` | Consistent Hashing, Rendezvous Hashing |
| Eviction | `EvictionPolicy<K>` | `LRUEvictionPolicy` | MRU, LFU, FIFO |
| Storage | `Database` | `InMemoryDatabase` | MySQL, Redis, MongoDB |

---

## Assumptions

- Keys are unique across the system.
- No real network communication — all nodes are in-memory objects (LLD exercise).
- Write policy is **write-through** — the database is always updated on every `put`.
- Each cache node has the same capacity (configured at startup).
- The number of nodes is fixed at startup; dynamic scaling is not handled.

---

## How to Run

```bash
cd distributed-cache/src
javac -d out $(find . -name "*.java")
java -cp out Main
```

### Sample Output

```
[DistributedCache] Initialized with 3 nodes, capacity=2 each.

  1. Cache MISS — loads from DB then caches
[MISS] key=user:1 → Node-2 — fetching from DB
[DB]   get(user:1) → Alice

  2. Cache HIT — served from cache
[HIT]  key=user:1 → Node-2

  3. put() — write-through to cache + DB
[DB]   put(user:4, Diana)
[PUT]  key=user:4 → Node-2

  4. LRU eviction — fill Node-2 beyond capacity
[Node-2] Evicted key: user:1
[PUT]  key=order:3 → Node-2
```
