import enums.PenState;

public abstract class Pen {
    private Refill refill;
    private PenState state;

    public Pen(Refill refill) {
        this.refill = refill;
        this.state = PenState.CLOSED;
    }

    public void start() {
        if (refill.isEmpty()) {
            state = PenState.EMPTY;
            System.out.println("Cannot open pen refill is empty.");
        } else {
            state = PenState.OPEN;
            System.out.println("Pen is now open.");
        }
    }

    public abstract void write(String text);

    public void close() {
        state = PenState.CLOSED;
        System.out.println("Pen is now closed.");
    }

    public void refill() {
        this.refill.refillInk();
        state = PenState.CLOSED;
        System.out.println("Refill restored to full capacity.");
    }

    protected Refill getRefill() {
        return refill;
    }

    protected PenState getState() {
        return state;
    }

    protected void setState(PenState state) {
        this.state = state;
    }
}
