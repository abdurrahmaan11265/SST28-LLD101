# Pluggable Rate Limiting System — LLD

## Class Diagram

<!-- Add UML diagram here -->

---

## Design & Approach

### Core Idea

Rate limiting is applied **only at the point where the external resource is called**, not at the incoming API layer. This means:

- Requests served from cache or local logic are never rate-limited.
- Quota is only consumed when the paid external resource is actually needed.

```
Client API Request
      │
      ▼
InternalService.processRequest()
      │
      ├── business logic runs first
      │
      ├── if no external call needed ──► return cached/local result (no quota consumed)
      │
      └── if external call needed
                │
                ▼
          RateLimiter.allowRequest(key)
                │
                ├── ALLOWED ──► ExternalResourceClient.call() ──► return result
                │
                └── DENIED  ──► throw RateLimitExceededException
```

---

### Algorithms

#### Fixed Window Counter

Time is split into fixed-size buckets (e.g. 0:00–0:59, 1:00–1:59).
A counter per `(key, window)` increments on each call and resets at the boundary.

```
Window:  |---- 0:00–0:59 ----|---- 1:00–1:59 ----|
Calls:        1  2  3  4  5        1  2  3  4  5
                          ↑ limit                ↑ limit
```

**Boundary burst problem**: a client can send `maxRequests` at 0:59 and another `maxRequests` at 1:00 — effectively `2× the limit` in a short span.

#### Sliding Window Counter

Tracks exact timestamps of every request in a rolling window of duration W.
On each call, timestamps older than `(now - W)` are evicted, then the count is checked.

```
now = 1:30
Window = [0:30 ──────────────────── 1:30]
              kept timestamps only in this range
```

No boundary burst — the window rolls continuously with time.

---

### Algorithm Trade-offs

| | Fixed Window | Sliding Window |
|---|---|---|
| Memory | O(1) per key | O(maxRequests) per key |
| Accuracy | Vulnerable to boundary burst | Exact rolling count |
| Speed | O(1) | O(1) amortised |
| Best for | High-limit, approximate enforcement | Low-limit, strict enforcement |

---

### Switching Algorithms

The `InternalService` depends only on the `RateLimiter` interface. Swap algorithms at construction time via the factory — no business logic changes required.

```java
// Fixed Window
RateLimiter limiter = RateLimiterFactory.create(Algorithm.FIXED_WINDOW, config);

// Sliding Window — same InternalService, zero code change inside it
RateLimiter limiter = RateLimiterFactory.create(Algorithm.SLIDING_WINDOW, config);

InternalService service = new InternalService(limiter, externalClient);
```

---

### Rate Limiting Key

The key can represent any dimension — the rate limiter is key-agnostic:

```java
service.processRequest("tenant-T1",   payload);  // per tenant
service.processRequest("user-U42",    payload);  // per user
service.processRequest("apikey-XYZ",  payload);  // per API key
service.processRequest("provider-aws",payload);  // per external provider
```

---

### Thread Safety

- `FixedWindowRateLimiter` — `synchronized` on the `WindowEntry` object per key.
- `SlidingWindowRateLimiter` — `synchronized` on the timestamp `Deque` per key.
- Both use `ConcurrentHashMap` to avoid a global lock — contention is at the key level, not system-wide.

---

### Adding a New Algorithm

1. Create a class that implements `RateLimiter`.
2. Add a constant to `RateLimiterFactory.Algorithm`.
3. Add a `case` in `RateLimiterFactory.create()`.

No other files change.

---

## Project Structure

```
rate-limiter/
├── README.md
└── src/
    ├── RateLimiter.java                 — core interface
    ├── RateLimitConfig.java             — immutable config (maxRequests + window)
    ├── RateLimitExceededException.java  — typed exception
    ├── FixedWindowRateLimiter.java      — algorithm 1
    ├── SlidingWindowRateLimiter.java    — algorithm 2
    ├── RateLimiterFactory.java          — creates limiter by algorithm name
    ├── ExternalResourceClient.java      — simulated paid external resource
    ├── InternalService.java             — business logic + rate limit check point
    └── RateLimiterMain.java             — demo + concurrency test
```

---

## How to Run

```bash
cd rate-limiter/src

javac -d out RateLimitConfig.java RateLimiter.java RateLimitExceededException.java \
  FixedWindowRateLimiter.java SlidingWindowRateLimiter.java RateLimiterFactory.java \
  ExternalResourceClient.java InternalService.java RateLimiterMain.java

java -cp out RateLimiterMain
```
