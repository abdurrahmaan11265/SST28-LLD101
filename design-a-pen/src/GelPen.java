import enums.PenState;

public class GelPen extends Pen {

    public GelPen(Refill refill) {
        super(refill);
    }

    @Override
    public void write(String text) {
        if (getState() != PenState.OPEN) {
            System.out.println("GelPen is not open. Call start() first.");
            return;
        }
        if (getRefill().isEmpty()) {
            setState(PenState.EMPTY);
            System.out.println("GelPen is out of ink.");
            return;
        }
        getRefill().decreaseInk(text.length());
        System.out.println("[GelPen] Writing: " + text);
    }
}
