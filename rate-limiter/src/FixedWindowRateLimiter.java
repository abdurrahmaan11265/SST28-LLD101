import java.util.concurrent.ConcurrentHashMap;

// Fixed Window Counter
//
// Time is divided into fixed-size windows (e.g. 0:00–0:59, 1:00–1:59…).
// A counter per (key, window) increments on each call and resets at window boundaries.
//
// Trade-off: simple and memory-efficient, but vulnerable to a "boundary burst" —
// a client can send maxRequests at the end of window N and maxRequests at the start
// of window N+1, effectively doubling the allowed rate over a short span.
public class FixedWindowRateLimiter implements RateLimiter {

    private final RateLimitConfig config;

    // key → WindowEntry
    private final ConcurrentHashMap<String, WindowEntry> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(RateLimitConfig config) {
        this.config = config;
    }

    @Override
    public synchronized boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        windows.compute(key, (k, entry) -> {
            if (entry == null || now >= entry.windowStart + config.getWindowDurationMs()) {
                return new WindowEntry(now, 0);   // new window
            }
            return entry;
        });

        WindowEntry entry = windows.get(key);
        synchronized (entry) {
            if (entry.count < config.getMaxRequests()) {
                entry.count++;
                return true;
            }
            return false;
        }
    }

    @Override
    public int getRemainingRequests(String key) {
        long now = System.currentTimeMillis();
        WindowEntry entry = windows.get(key);
        if (entry == null || now >= entry.windowStart + config.getWindowDurationMs()) {
            return config.getMaxRequests();
        }
        return Math.max(0, config.getMaxRequests() - entry.count);
    }

    private static class WindowEntry {
        long windowStart;
        int count;

        WindowEntry(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
