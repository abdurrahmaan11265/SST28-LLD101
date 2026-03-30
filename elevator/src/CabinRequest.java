// Internal request: a user inside the elevator pressing a destination floor button
public class CabinRequest {
    private final int destinationFloor;
    private final int elevatorId;

    public CabinRequest(int destinationFloor, int elevatorId) {
        this.destinationFloor = destinationFloor;
        this.elevatorId = elevatorId;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    @Override
    public String toString() {
        return "CabinRequest{elevatorId=" + elevatorId + ", destinationFloor=" + destinationFloor + "}";
    }
}
