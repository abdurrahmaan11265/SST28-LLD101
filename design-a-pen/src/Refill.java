import enums.InkType;

public class Refill {
    private InkType inkType;
    private int inkLevel;
    private int capacity;

    public Refill(InkType inkType, int capacity) {
        this.inkType = inkType;
        this.capacity = capacity;
        this.inkLevel = capacity;
    }

    public boolean isEmpty() {
        return inkLevel <= 0;
    }

    public void decreaseInk(int amount) {
        inkLevel = Math.max(0, inkLevel - amount);
    }

    public void refillInk() {
        inkLevel = capacity;
    }

    public InkType getInkType() {
        return inkType;
    }

    public int getInkLevel() {
        return inkLevel;
    }

    public int getCapacity() {
        return capacity;
    }
}
