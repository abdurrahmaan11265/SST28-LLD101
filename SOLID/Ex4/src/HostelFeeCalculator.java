import java.util.*;

public class HostelFeeCalculator {
    private final FakeBookingRepo repo;
    private final List<FeeComponent> components;

    public HostelFeeCalculator(FakeBookingRepo repo, List<FeeComponent> components) { 
        this.repo = repo; 
        this.components = components;
    }

    // OCP violation: switch + add-on branching + printing + persistence.
    public void process(BookingRequest req) {
        Money monthly = calculateMonthly(req);
        Money deposit = new Money(5000.00);

        ReceiptPrinter.print(req, monthly, deposit);

        String bookingId = "H-" + (7000 + new Random(1).nextInt(1000)); // deterministic-ish
        repo.save(bookingId, req, monthly, deposit);
    }

    private Money calculateMonthly(BookingRequest req) {
        double amount = 0.0;
        for(FeeComponent component: components) {
            amount += component.monthlyAmount();
        }
        return new Money(amount);
    }
}
