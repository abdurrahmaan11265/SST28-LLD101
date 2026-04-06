// Factory — creates the right RateLimiter given an algorithm name.
// Callers use the factory so the concrete type never leaks into business code.
public class RateLimiterFactory {

    public enum Algorithm {
        FIXED_WINDOW,
        SLIDING_WINDOW
    }

    public static RateLimiter create(Algorithm algorithm, RateLimitConfig config) {
        return switch (algorithm) {
            case FIXED_WINDOW   -> new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW -> new SlidingWindowRateLimiter(config);
        };
    }
}
