import enums.PaymentMethod;
import enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
    private final String paymentId;
    private final double amount;
    private final PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentTime;

    public Payment(double amount, PaymentMethod method) {
        this.paymentId = UUID.randomUUID().toString();
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    // Simulates payment gateway call — returns true on success
    public boolean process() {
        // In production this calls an external payment gateway
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.paymentTime = LocalDateTime.now();
        this.status = PaymentStatus.SUCCESS;  // simulate success
        System.out.println("  [Payment] " + method + " | amount=₹" + amount
                + " | txn=" + transactionId + " | " + status);
        return status == PaymentStatus.SUCCESS;
    }

    public void refund() {
        if (status == PaymentStatus.SUCCESS) {
            status = PaymentStatus.REFUNDED;
            System.out.println("  [Payment] Refunded txn=" + transactionId);
        }
    }

    public String getPaymentId()       { return paymentId; }
    public double getAmount()          { return amount; }
    public PaymentMethod getMethod()   { return method; }
    public PaymentStatus getStatus()   { return status; }
    public String getTransactionId()   { return transactionId; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
}
