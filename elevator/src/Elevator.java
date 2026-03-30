import enums.Direction;
import enums.ElevatorState;
import java.util.TreeSet;

public class Elevator {
    private final int elevatorId;
    private int currentFloor;
    private ElevatorState state;
    private Direction direction;
    private final int capacity;
    private int currentLoad;
    // Two queues for LOOK algorithm: floors above and below current
    private final TreeSet<Integer> upQueue;   // floors to serve going UP
    private final TreeSet<Integer> downQueue; // floors to serve going DOWN

    public Elevator(int elevatorId, int capacity) {
        this.elevatorId = elevatorId;
        this.capacity = capacity;
        this.currentFloor = 0;
        this.state = ElevatorState.IDLE;
        this.direction = Direction.IDLE;
        this.currentLoad = 0;
        this.upQueue = new TreeSet<>();
        this.downQueue = new TreeSet<>((a, b) -> b - a); // descending for DOWN
    }

    // Add a floor to the appropriate queue based on where it is relative to current floor
    public void addFloorToQueue(int floor) {
        if (floor >= currentFloor) {
            upQueue.add(floor);
        } else {
            downQueue.add(floor);
        }
    }

    // LOOK algorithm: move in current direction, pick next floor from queue
    public void move() {
        if (upQueue.isEmpty() && downQueue.isEmpty()) {
            state = ElevatorState.IDLE;
            direction = Direction.IDLE;
            return;
        }

        state = ElevatorState.MOVING;

        if (direction == Direction.UP || direction == Direction.IDLE) {
            if (!upQueue.isEmpty()) {
                direction = Direction.UP;
                currentFloor = upQueue.first();
                upQueue.remove(currentFloor);
            } else {
                // No more floors above — reverse
                direction = Direction.DOWN;
                currentFloor = downQueue.first();
                downQueue.remove(currentFloor);
            }
        } else {
            if (!downQueue.isEmpty()) {
                direction = Direction.DOWN;
                currentFloor = downQueue.first();
                downQueue.remove(currentFloor);
            } else {
                // No more floors below — reverse
                direction = Direction.UP;
                currentFloor = upQueue.first();
                upQueue.remove(currentFloor);
            }
        }

        state = ElevatorState.STOPPED;
    }

    public void openDoor() {
        System.out.println("  [Elevator " + elevatorId + "] Door OPEN at floor " + currentFloor);
    }

    public void closeDoor() {
        System.out.println("  [Elevator " + elevatorId + "] Door CLOSE at floor " + currentFloor);
    }

    public boolean canAccommodate() {
        return currentLoad < capacity;
    }

    public boolean hasPendingRequests() {
        return !upQueue.isEmpty() || !downQueue.isEmpty();
    }

    // Estimate cost to serve a floor (used by selection strategy)
    public int estimateCost(int targetFloor) {
        return Math.abs(currentFloor - targetFloor);
    }

    // Getters
    public int getElevatorId()     { return elevatorId; }
    public int getCurrentFloor()   { return currentFloor; }
    public ElevatorState getState(){ return state; }
    public Direction getDirection(){ return direction; }
    public int getCurrentLoad()    { return currentLoad; }
    public int getCapacity()       { return capacity; }
    public TreeSet<Integer> getUpQueue()   { return upQueue; }
    public TreeSet<Integer> getDownQueue() { return downQueue; }

    public void setCurrentFloor(int floor) { this.currentFloor = floor; }
    public void setState(ElevatorState state) { this.state = state; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public void incrementLoad() { currentLoad++; }
    public void decrementLoad() { if (currentLoad > 0) currentLoad--; }
}
