// Internal service that sits between the client API and the external resource.
// Business logic runs first — rate limiting is only checked when an external
// call is actually needed. The RateLimiter is injected, so the algorithm can
// be switched without touching this class (Open/Closed principle).
public class InternalService {

    private final RateLimiter rateLimiter;
    private final ExternalResourceClient externalClient;

    public InternalService(RateLimiter rateLimiter, ExternalResourceClient externalClient) {
        this.rateLimiter = rateLimiter;
        this.externalClient = externalClient;
    }

    // key  — the rate-limiting dimension (tenantId, userId, apiKey, …)
    // data — request payload
    public String processRequest(String key, String data) {

        // ── Business logic ───────────────────────────────────────────────────
        // Decide whether an external call is actually needed.
        // Requests that don't need the external resource skip the rate limiter.
        boolean needsExternalCall = requiresExternalResource(data);

        if (!needsExternalCall) {
            System.out.println("  [InternalService] Serving from cache/local — no external call needed.");
            return "CACHED:" + data;
        }

        // ── Rate limit check ─────────────────────────────────────────────────
        if (!rateLimiter.allowRequest(key)) {
            int remaining = rateLimiter.getRemainingRequests(key);
            System.out.println("  [InternalService] DENIED for key=" + key
                    + " (remaining=" + remaining + ")");
            throw new RateLimitExceededException(key);
        }

        int remaining = rateLimiter.getRemainingRequests(key);
        System.out.println("  [InternalService] ALLOWED for key=" + key
                + " (remaining=" + remaining + ")");

        // ── External call ────────────────────────────────────────────────────
        return externalClient.call(data);
    }

    // Simulate business logic: requests containing "cached" don't need the external resource
    private boolean requiresExternalResource(String data) {
        return !data.contains("cached");
    }
}
