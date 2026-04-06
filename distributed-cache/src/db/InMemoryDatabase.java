package db;

import java.util.HashMap;
import java.util.Map;

// Stub database backed by a HashMap.
// Used for testing and demonstration; replace with a real DB in production.
public class InMemoryDatabase implements Database {

    private final Map<String, String> store = new HashMap<>();

    // Pre-populate with seed data so cache-miss paths can be exercised.
    public InMemoryDatabase() {
        store.put("user:1",    "Alice");
        store.put("user:2",    "Bob");
        store.put("user:3",    "Charlie");
        store.put("product:1", "Laptop");
        store.put("product:2", "Phone");
    }

    @Override
    public String get(String key) {
        String value = store.get(key);
        System.out.println("[DB] get(" + key + ") → " + (value != null ? value : "null (not found)"));
        return value;
    }

    @Override
    public void put(String key, String value) {
        store.put(key, value);
        System.out.println("[DB] put(" + key + ", " + value + ")");
    }
}
