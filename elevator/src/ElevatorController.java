import enums.Direction;
import enums.ElevatorState;

// Wraps an Elevator and drives its movement step by step
public class ElevatorController {
    private final Elevator elevator;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
    }

    public void addFloorRequest(int floor) {
        elevator.addFloorToQueue(floor);
    }

    // Process one step: move to the next pending floor, open/close doors
    public void processNextRequest() {
        if (!elevator.hasPendingRequests()) {
            return;
        }

        int before = elevator.getCurrentFloor();
        elevator.move();
        int after = elevator.getCurrentFloor();

        System.out.println("[Elevator " + elevator.getElevatorId() + "] "
                + before + " -> " + after
                + "  (direction=" + elevator.getDirection()
                + ", state=" + elevator.getState() + ")");

        elevator.openDoor();
        elevator.closeDoor();
    }

    // Run until all pending requests are served
    public void processAllRequests() {
        while (elevator.hasPendingRequests()) {
            processNextRequest();
        }
        elevator.setState(ElevatorState.IDLE);
        elevator.setDirection(Direction.IDLE);
    }

    public Elevator getElevator() {
        return elevator;
    }

    public int getCurrentFloor() {
        return elevator.getCurrentFloor();
    }

    public ElevatorState getState() {
        return elevator.getState();
    }

    public int getElevatorId() {
        return elevator.getElevatorId();
    }
}
