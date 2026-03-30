import enums.SeatStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final double basePrice;

    // showSeatId -> ShowSeat (one entry per physical seat in the screen)
    private final Map<String, ShowSeat> showSeats;

    public Show(String showId, Movie movie, Screen screen,
                LocalDateTime startTime, double basePrice) {
        this.showId = showId;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(movie.getDurationMins());
        this.basePrice = basePrice;
        this.showSeats = new HashMap<>();
        initShowSeats();
    }

    // Create a ShowSeat for every physical seat in the screen
    private void initShowSeats() {
        for (Seat seat : screen.getSeats()) {
            double price = priceFor(seat);
            String showSeatId = showId + "-" + seat.getSeatId();
            showSeats.put(showSeatId, new ShowSeat(showSeatId, seat, price));
        }
    }

    private double priceFor(Seat seat) {
        return switch (seat.getType()) {
            case REGULAR -> basePrice;
            case PREMIUM -> basePrice * 1.5;
            case VIP     -> basePrice * 2.5;
        };
    }

    // Returns ShowSeats that are currently AVAILABLE
    public List<ShowSeat> getAvailableSeats() {
        List<ShowSeat> available = new ArrayList<>();
        for (ShowSeat ss : showSeats.values()) {
            if (ss.getStatus() == SeatStatus.AVAILABLE) available.add(ss);
        }
        return available;
    }

    // Try to lock the requested seats for a user atomically.
    // Returns the locked ShowSeats, or throws if any seat is unavailable.
    public List<ShowSeat> lockSeats(List<String> seatIds, String userId) {
        List<ShowSeat> toBook = new ArrayList<>();
        for (String seatId : seatIds) {
            String showSeatId = showId + "-" + seatId;
            ShowSeat ss = showSeats.get(showSeatId);
            if (ss == null) throw new IllegalArgumentException("Seat not found: " + seatId);
            toBook.add(ss);
        }

        List<ShowSeat> locked = new ArrayList<>();
        for (ShowSeat ss : toBook) {
            if (!ss.lock(userId)) {
                // Roll back any locks already acquired in this batch
                for (ShowSeat done : locked) done.unlock();
                throw new IllegalStateException("Seat " + ss.getSeat() + " is no longer available.");
            }
            locked.add(ss);
        }
        return locked;
    }

    public String getShowId()           { return showId; }
    public Movie getMovie()             { return movie; }
    public Screen getScreen()           { return screen; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime()   { return endTime; }
    public double getBasePrice()        { return basePrice; }
    public Map<String, ShowSeat> getShowSeats() { return showSeats; }
}
