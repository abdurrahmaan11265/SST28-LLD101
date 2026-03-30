import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userId;
    private final String name;
    private final String email;
    private final String phone;
    private final List<Booking> bookings;

    public User(String userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void viewBookings() {
        System.out.println("\n[" + name + "'s Bookings]");
        if (bookings.isEmpty()) {
            System.out.println("  No bookings found.");
            return;
        }
        for (Booking b : bookings) {
            System.out.println("  " + b.getBookingId()
                    + " | " + b.getShow().getMovie().getTitle()
                    + " | " + b.getStatus());
        }
    }

    public void cancelBooking(String bookingId) {
        bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst()
                .ifPresent(b -> {
                    b.cancel();
                    System.out.println("Booking " + bookingId + " cancelled.");
                });
    }

    public String getUserId() { return userId; }
    public String getName()   { return name; }
    public String getEmail()  { return email; }
    public String getPhone()  { return phone; }
    public List<Booking> getBookings() { return bookings; }
}
