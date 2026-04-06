public class RateLimitExceededException extends RuntimeException {
    private final String key;

    public RateLimitExceededException(String key) {
        super("Rate limit exceeded for key: " + key);
        this.key = key;
    }

    public String getKey() { return key; }
}
