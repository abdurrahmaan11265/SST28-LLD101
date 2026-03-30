import enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final User user;
    private final Show show;
    private final List<ShowSeat> seats;
    private final LocalDateTime bookingTime;
    private BookingStatus status;
    private Payment payment;

    public Booking(String bookingId, User user, Show show, List<ShowSeat> seats) {
        this.bookingId = bookingId;
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }

    // Attach payment and confirm all seats
    public void confirm(Payment payment) {
        this.payment = payment;
        for (ShowSeat ss : seats) {
            ss.book(user.getUserId());
        }
        this.status = BookingStatus.CONFIRMED;
    }

    // Cancel booking — unlock/unbook all seats and trigger refund
    public void cancel() {
        if (status == BookingStatus.CONFIRMED) {
            for (ShowSeat ss : seats) ss.unlock();
            if (payment != null) payment.refund();
        } else if (status == BookingStatus.PENDING) {
            for (ShowSeat ss : seats) ss.unlock();
        }
        this.status = BookingStatus.CANCELLED;
    }

    public void printTickets() {
        System.out.println("\n  === Booking Confirmation ===");
        System.out.println("  Booking ID : " + bookingId);
        System.out.println("  Movie      : " + show.getMovie().getTitle());
        System.out.println("  Show       : " + show.getStartTime());
        System.out.println("  User       : " + user.getName());
        System.out.println("  Status     : " + status);
        System.out.println("  Seats      :");
        for (ShowSeat ss : seats) {
            System.out.println("    " + ss.getSeat() + " | ₹" + ss.getPrice()
                    + " | " + ss.getStatus());
        }
        double total = seats.stream().mapToDouble(ShowSeat::getPrice).sum();
        System.out.println("  Total      : ₹" + total);
        System.out.println("  ============================\n");
    }

    public String getBookingId()     { return bookingId; }
    public User getUser()            { return user; }
    public Show getShow()            { return show; }
    public List<ShowSeat> getSeats() { return seats; }
    public BookingStatus getStatus() { return status; }
    public Payment getPayment()      { return payment; }
}
