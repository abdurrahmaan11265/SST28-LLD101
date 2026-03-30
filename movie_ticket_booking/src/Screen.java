import java.util.ArrayList;
import java.util.List;

public class Screen {
    private final String screenId;
    private final int screenNumber;
    private final List<Seat> seats;

    public Screen(String screenId, int screenNumber) {
        this.screenId = screenId;
        this.screenNumber = screenNumber;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public int getTotalSeats() {
        return seats.size();
    }

    public String getScreenId()    { return screenId; }
    public int getScreenNumber()   { return screenNumber; }
}
