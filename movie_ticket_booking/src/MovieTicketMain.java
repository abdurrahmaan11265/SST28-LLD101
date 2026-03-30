import enums.PaymentMethod;
import enums.SeatType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MovieTicketMain {
    public static void main(String[] args) throws InterruptedException {

        BookingService service = new BookingService();
        Admin admin = new Admin("admin-1");

        // ── Admin: setup ─────────────────────────────────────────────────────

        Movie movie = new Movie("M1", "Interstellar", 169, "Sci-Fi", "English",
                "A journey through space and time.");
        admin.addMovie(service, movie);

        Theater theater = new Theater("T1", "PVR Cinemas", "Mumbai", "Andheri West");
        Screen screen = new Screen("S1", 1);

        // Add seats: rows A-B (REGULAR), C (PREMIUM), D (VIP)
        String[] regularRows = {"A", "B"};
        for (String row : regularRows) {
            for (int i = 1; i <= 5; i++) {
                screen.addSeat(new Seat("seat-" + row + i, row, i, SeatType.REGULAR));
            }
        }
        for (int i = 1; i <= 4; i++) {
            screen.addSeat(new Seat("seat-C" + i, "C", i, SeatType.PREMIUM));
        }
        for (int i = 1; i <= 2; i++) {
            screen.addSeat(new Seat("seat-D" + i, "D", i, SeatType.VIP));
        }
        theater.addScreen(screen);
        admin.addTheater(service, theater);

        Show show = new Show("SH1", movie, screen,
                LocalDateTime.of(2026, 4, 1, 18, 30), 200.0);
        admin.scheduleShow(service, show);

        // ── Customer: browse and book ─────────────────────────────────────────

        User alice = new User("U1", "Alice", "alice@mail.com", "9000000001");
        User bob   = new User("U2", "Bob",   "bob@mail.com",   "9000000002");

        System.out.println("\n--- Available seats for SH1 ---");
        List<ShowSeat> available = service.getAvailableSeats("SH1");
        available.forEach(ss -> System.out.print(ss.getSeat() + " "));
        System.out.println();

        // Alice books 2 REGULAR + 1 PREMIUM
        Booking aliceBooking = service.bookTickets(
                alice, "SH1",
                List.of("seat-A1", "seat-A2", "seat-C1"),
                PaymentMethod.UPI);
        aliceBooking.printTickets();
        alice.viewBookings();

        // Bob books VIP seats
        Booking bobBooking = service.bookTickets(
                bob, "SH1",
                List.of("seat-D1", "seat-D2"),
                PaymentMethod.CREDIT_CARD);
        bobBooking.printTickets();

        // ── Concurrency test: two users race for the same seat ───────────────

        System.out.println("=== Concurrency Test: Alice and Bob race for seat-B1 ===");
        User charlie = new User("U3", "Charlie", "charlie@mail.com", "9000000003");
        User diana   = new User("U4", "Diana",   "diana@mail.com",   "9000000004");

        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.submit(() -> {
            try {
                Booking b = service.bookTickets(charlie, "SH1",
                        List.of("seat-B1"), PaymentMethod.DEBIT_CARD);
                b.printTickets();
            } catch (Exception e) {
                System.out.println("[Charlie] Booking failed: " + e.getMessage());
            }
        });

        pool.submit(() -> {
            try {
                Booking b = service.bookTickets(diana, "SH1",
                        List.of("seat-B1"), PaymentMethod.WALLET);
                b.printTickets();
            } catch (Exception e) {
                System.out.println("[Diana]   Booking failed: " + e.getMessage());
            }
        });

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        // ── Alice cancels her booking ─────────────────────────────────────────

        System.out.println("\n--- Alice cancels her booking ---");
        service.cancelBooking(alice, aliceBooking.getBookingId());
        alice.viewBookings();

        System.out.println("\n--- Available seats after cancellation ---");
        service.getAvailableSeats("SH1")
               .forEach(ss -> System.out.print(ss.getSeat() + " "));
        System.out.println();
    }
}
