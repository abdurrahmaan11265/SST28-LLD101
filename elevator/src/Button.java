import enums.Direction;

public class Button {
    private boolean isPressed;
    private final Direction direction;

    public Button(Direction direction) {
        this.direction = direction;
        this.isPressed = false;
    }

    public void press() {
        this.isPressed = true;
    }

    public void reset() {
        this.isPressed = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public Direction getDirection() {
        return direction;
    }
}
