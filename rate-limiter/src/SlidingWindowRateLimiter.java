import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

// Sliding Window Counter
//
// Tracks exact timestamps of each request in a rolling window of duration W.
// On each call, timestamps older than (now - W) are evicted, then the count is checked.
//
// Trade-off: eliminates the boundary-burst problem of Fixed Window, but uses more
// memory (O(maxRequests) per key) since all timestamps within the window are stored.
// For very high limits (e.g. 10,000/min) the deque can grow large — use Sliding Log
// or approximate counters in that case.
public class SlidingWindowRateLimiter implements RateLimiter {

    private final RateLimitConfig config;

    // key → deque of request timestamps (oldest first)
    private final ConcurrentHashMap<String, Deque<Long>> logs = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(RateLimitConfig config) {
        this.config = config;
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = logs.computeIfAbsent(key, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            evictExpired(timestamps, now);

            if (timestamps.size() < config.getMaxRequests()) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }

    @Override
    public int getRemainingRequests(String key) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = logs.get(key);
        if (timestamps == null) return config.getMaxRequests();

        synchronized (timestamps) {
            evictExpired(timestamps, now);
            return Math.max(0, config.getMaxRequests() - timestamps.size());
        }
    }

    private void evictExpired(Deque<Long> timestamps, long now) {
        long cutoff = now - config.getWindowDurationMs();
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.pollFirst();
        }
    }
}
