import enums.Direction;

public class Floor {
    private final int floorNumber;
    private final Button upButton;
    private final Button downButton;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upButton = new Button(Direction.UP);
        this.downButton = new Button(Direction.DOWN);
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void pressUpButton() {
        upButton.press();
    }

    public void pressDownButton() {
        downButton.press();
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }
}
