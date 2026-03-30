import enums.SeatType;

public class Seat {
    private final String seatId;
    private final String row;
    private final int seatNumber;
    private final SeatType type;

    public Seat(String seatId, String row, int seatNumber, SeatType type) {
        this.seatId = seatId;
        this.row = row;
        this.seatNumber = seatNumber;
        this.type = type;
    }

    public String getSeatId()    { return seatId; }
    public String getRow()       { return row; }
    public int getSeatNumber()   { return seatNumber; }
    public SeatType getType()    { return type; }

    @Override
    public String toString() {
        return row + seatNumber + "(" + type + ")";
    }
}
