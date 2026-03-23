import enums.PenState;

public class FountainPen extends Pen {

    public FountainPen(Refill refill) {
        super(refill);
    }

    @Override
    public void write(String text) {
        if (getState() != PenState.OPEN) {
            System.out.println("FountainPen is not open. Call start() first.");
            return;
        }
        if (getRefill().isEmpty()) {
            setState(PenState.EMPTY);
            System.out.println("FountainPen is out of ink.");
            return;
        }
        getRefill().decreaseInk(text.length());
        System.out.println("[FountainPen] Writing: " + text);
    }
}
