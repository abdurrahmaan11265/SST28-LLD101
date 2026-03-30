import enums.SeatStatus;
import java.time.LocalDateTime;

// Represents one seat's state for a specific show.
// This is the critical class for concurrency — all state transitions are synchronized.
public class ShowSeat {
    private final String showSeatId;
    private final Seat seat;
    private final double price;

    private SeatStatus status;
    private String lockedByUserId;
    private LocalDateTime lockExpiry;

    // A lock window of 10 minutes — payment must complete within this time
    private static final int LOCK_MINUTES = 10;

    public ShowSeat(String showSeatId, Seat seat, double price) {
        this.showSeatId = showSeatId;
        this.seat = seat;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    // Atomically lock the seat for a user during the payment window.
    // Returns true if lock was acquired, false if seat is already taken.
    public synchronized boolean lock(String userId) {
        if (status == SeatStatus.AVAILABLE || isLockExpired()) {
            status = SeatStatus.LOCKED;
            lockedByUserId = userId;
            lockExpiry = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
            return true;
        }
        return false;
    }

    // Release the lock — called on payment failure or timeout.
    public synchronized void unlock() {
        if (status == SeatStatus.LOCKED) {
            status = SeatStatus.AVAILABLE;
            lockedByUserId = null;
            lockExpiry = null;
        }
    }

    // Confirm the booking — only the user who holds the lock can book.
    public synchronized boolean book(String userId) {
        if (status == SeatStatus.LOCKED
                && userId.equals(lockedByUserId)
                && !isLockExpired()) {
            status = SeatStatus.BOOKED;
            return true;
        }
        return false;
    }

    private boolean isLockExpired() {
        return lockExpiry != null && LocalDateTime.now().isAfter(lockExpiry);
    }

    public String getShowSeatId()     { return showSeatId; }
    public Seat getSeat()             { return seat; }
    public double getPrice()          { return price; }
    public SeatStatus getStatus()     { return status; }
    public String getLockedByUserId() { return lockedByUserId; }
}
