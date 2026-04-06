import java.util.concurrent.TimeUnit;

// Immutable config: limit + window size.
// Example: new RateLimitConfig(100, 1, TimeUnit.MINUTES) → 100 requests per minute
public class RateLimitConfig {
    private final int maxRequests;
    private final long windowDurationMs;

    public RateLimitConfig(int maxRequests, long windowDuration, TimeUnit unit) {
        if (maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be > 0");
        if (windowDuration <= 0) throw new IllegalArgumentException("windowDuration must be > 0");
        this.maxRequests = maxRequests;
        this.windowDurationMs = unit.toMillis(windowDuration);
    }

    public int getMaxRequests()        { return maxRequests; }
    public long getWindowDurationMs()  { return windowDurationMs; }
}
