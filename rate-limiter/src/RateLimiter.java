// Core interface — the only thing internal services depend on.
// Swap any algorithm behind this without touching business logic.
public interface RateLimiter {

    // Returns true if the call is allowed, false if it should be denied.
    boolean allowRequest(String key);

    // Remaining allowed calls in the current window (informational).
    int getRemainingRequests(String key);
}
