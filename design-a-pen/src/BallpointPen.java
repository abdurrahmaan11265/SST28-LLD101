import enums.PenState;

public class BallpointPen extends Pen {

    public BallpointPen(Refill refill) {
        super(refill);
    }

    @Override
    public void write(String text) {
        if (getState() != PenState.OPEN) {
            System.out.println("BallpointPen is not open. Call start() first.");
            return;
        }
        if (getRefill().isEmpty()) {
            setState(PenState.EMPTY);
            System.out.println("BallpointPen is out of ink.");
            return;
        }
        getRefill().decreaseInk(text.length());
        System.out.println("[BallpointPen] Writing: " + text);
    }
}
