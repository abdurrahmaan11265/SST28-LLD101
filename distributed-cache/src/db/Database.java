package db;

// Abstraction over the backing store.
// Assumption: the real database is always up-to-date (write-through cache).
// Any Database implementation (SQL, NoSQL, in-memory stub) can be injected.
public interface Database {
    String get(String key);
    void   put(String key, String value);
}
