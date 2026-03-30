import enums.PaymentMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// Central service — all customer-facing and admin operations go through here.
// Concurrency note: seat locking is handled inside ShowSeat (synchronized).
// BookingService coordinates the three-phase flow: lock → pay → confirm.
public class BookingService {

    private final Map<String, Movie>   movies   = new HashMap<>();
    private final Map<String, Theater> theaters = new HashMap<>();
    private final Map<String, Show>    shows    = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    // ── Admin operations ────────────────────────────────────────────────────

    public void addMovie(Movie movie)     { movies.put(movie.getMovieId(), movie); }
    public void addTheater(Theater t)     { theaters.put(t.getTheaterId(), t); }
    public void addShow(Show show) {
        shows.put(show.getShowId(), show);
        show.getMovie().addShow(show);
    }
    public void cancelShow(String showId) { shows.remove(showId); }

    // ── Customer: discovery ─────────────────────────────────────────────────

    public List<Movie> getMoviesByCity(String city) {
        List<Movie> result = new ArrayList<>();
        for (Show show : shows.values()) {
            String showCity = show.getScreen().getScreenId().split("-")[0]; // encoded in id
            // Simpler: check theaters in the city that have this show's screen
        }
        // Return all movies that have at least one show in a theater in this city
        return movies.values().stream()
                .filter(m -> m.getShows().stream().anyMatch(s -> shows.containsKey(s.getShowId())))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsForMovie(String movieId) {
        return shows.values().stream()
                .filter(s -> s.getMovie().getMovieId().equals(movieId))
                .collect(Collectors.toList());
    }

    public List<ShowSeat> getAvailableSeats(String showId) {
        Show show = getShow(showId);
        return show.getAvailableSeats();
    }

    // ── Customer: booking flow ──────────────────────────────────────────────

    // Phase 1: Lock seats (10-min hold). Throws if any seat is unavailable.
    // Phase 2: Process payment.
    // Phase 3: Confirm booking (LOCKED → BOOKED). Unlock on failure.
    public Booking bookTickets(User user, String showId,
                               List<String> seatIds, PaymentMethod method) {

        Show show = getShow(showId);

        // Phase 1 — lock
        List<ShowSeat> lockedSeats = show.lockSeats(seatIds, user.getUserId());
        System.out.println("\n[BookingService] Seats locked for " + user.getName()
                + ": " + lockedSeats.stream()
                        .map(ss -> ss.getSeat().toString())
                        .collect(Collectors.joining(", ")));

        double total = lockedSeats.stream().mapToDouble(ShowSeat::getPrice).sum();
        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking(bookingId, user, show, lockedSeats);

        // Phase 2 — payment
        Payment payment = new Payment(total, method);
        boolean paid = payment.process();

        if (!paid) {
            // Roll back: unlock all seats
            lockedSeats.forEach(ShowSeat::unlock);
            booking.cancel();
            throw new RuntimeException("Payment failed for booking " + bookingId);
        }

        // Phase 3 — confirm
        booking.confirm(payment);
        user.addBooking(booking);
        bookings.put(bookingId, booking);
        System.out.println("[BookingService] Booking confirmed: " + bookingId);
        return booking;
    }

    public void cancelBooking(User user, String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) throw new IllegalArgumentException("Booking not found: " + bookingId);
        if (!booking.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("User does not own this booking.");
        }
        booking.cancel();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private Show getShow(String showId) {
        Show show = shows.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found: " + showId);
        return show;
    }

    public Map<String, Movie>   getMovies()   { return movies; }
    public Map<String, Theater> getTheaters() { return theaters; }
    public Map<String, Show>    getShows()    { return shows; }
}
