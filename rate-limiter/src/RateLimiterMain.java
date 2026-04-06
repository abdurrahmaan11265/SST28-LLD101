import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class RateLimiterMain {

    public static void main(String[] args) throws InterruptedException {

        RateLimitConfig config = new RateLimitConfig(5, 1, TimeUnit.MINUTES);
        ExternalResourceClient client = new ExternalResourceClient();

        // ── Demo 1: Fixed Window ─────────────────────────────────────────────
        System.out.println("==============================");
        System.out.println(" FIXED WINDOW (5 req/min)");
        System.out.println("==============================");
        runDemo(RateLimiterFactory.create(RateLimiterFactory.Algorithm.FIXED_WINDOW, config), client);

        // ── Demo 2: Sliding Window ───────────────────────────────────────────
        System.out.println("\n==============================");
        System.out.println(" SLIDING WINDOW (5 req/min)");
        System.out.println("==============================");
        runDemo(RateLimiterFactory.create(RateLimiterFactory.Algorithm.SLIDING_WINDOW, config), client);

        // ── Demo 3: Request that skips rate limiter ──────────────────────────
        System.out.println("\n==============================");
        System.out.println(" CACHED REQUEST (no external call)");
        System.out.println("==============================");
        RateLimiter fw = RateLimiterFactory.create(RateLimiterFactory.Algorithm.FIXED_WINDOW, config);
        InternalService svc = new InternalService(fw, client);
        // Even after exhausting the limit, a cached request goes through
        for (int i = 0; i < 6; i++) {
            try { svc.processRequest("T1", "data-" + i); } catch (RateLimitExceededException e) { System.out.println("  >> " + e.getMessage()); }
        }
        System.out.println("\n  Sending cached request after limit exceeded:");
        String result = svc.processRequest("T1", "cached-payload");
        System.out.println("  Result: " + result);

        // ── Demo 4: Concurrency — 10 threads race for 5 slots ────────────────
        System.out.println("\n==============================");
        System.out.println(" CONCURRENCY TEST (10 threads, limit=5)");
        System.out.println("==============================");
        concurrencyTest();
    }

    private static void runDemo(RateLimiter limiter, ExternalResourceClient client) {
        InternalService service = new InternalService(limiter, client);
        String key = "tenant-T1";

        // 7 requests — first 5 allowed, last 2 denied
        for (int i = 1; i <= 7; i++) {
            System.out.println("\nRequest #" + i + " for " + key);
            try {
                String result = service.processRequest(key, "payload-" + i);
                System.out.println("  Result: " + result);
            } catch (RateLimitExceededException e) {
                System.out.println("  >> " + e.getMessage());
            }
        }
    }

    private static void concurrencyTest() throws InterruptedException {
        RateLimitConfig config = new RateLimitConfig(5, 1, TimeUnit.MINUTES);
        RateLimiter limiter = RateLimiterFactory.create(RateLimiterFactory.Algorithm.SLIDING_WINDOW, config);
        ExternalResourceClient client = new ExternalResourceClient();
        InternalService service = new InternalService(limiter, client);

        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        int[] allowed = {0};
        int[] denied = {0};

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            pool.submit(() -> {
                try {
                    startGate.await(); // all threads start simultaneously
                    service.processRequest("shared-key", "concurrent-" + idx);
                    synchronized (allowed) { allowed[0]++; }
                } catch (RateLimitExceededException e) {
                    synchronized (denied) { denied[0]++; }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startGate.countDown(); // release all threads at once
        pool.shutdown();
        pool.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);

        System.out.println("\n  Allowed: " + allowed[0] + " / Denied: " + denied[0]
                + " (expected 5 allowed, 5 denied)");
    }
}
