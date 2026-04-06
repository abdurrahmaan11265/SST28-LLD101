// Simulates the paid external resource (e.g. an AI API, payment gateway, SMS provider).
// In production this would be an HTTP client; here it just prints.
public class ExternalResourceClient {

    public String call(String payload) {
        System.out.println("    [ExternalResource] Called with: " + payload);
        return "OK:" + payload;
    }
}
