import enums.Direction;

public class ElevatorMain {
    public static void main(String[] args) {

        // --- Setup ---
        ElevatorSystem system = new ElevatorSystem(new ScanStrategy());

        // 3 elevators, capacity 8 each
        for (int i = 1; i <= 3; i++) {
            system.addElevator(new ElevatorController(new Elevator(i, 8)));
        }

        // 10-floor building
        for (int i = 0; i <= 9; i++) {
            system.addFloor(new Floor(i));
        }

        system.status();

        // --- Scenario 1: External (hall) requests ---
        system.handleExternalRequest(new HallCall(3, Direction.UP));   // floor 3 going up
        system.handleExternalRequest(new HallCall(7, Direction.DOWN)); // floor 7 going down
        system.handleExternalRequest(new HallCall(1, Direction.UP));   // floor 1 going up

        // --- Scenario 2: Internal (cabin) requests ---
        system.handleInternalRequest(new CabinRequest(9, 1)); // elevator 1, destination floor 9
        system.handleInternalRequest(new CabinRequest(2, 2)); // elevator 2, destination floor 2
        system.handleInternalRequest(new CabinRequest(5, 3)); // elevator 3, destination floor 5

        // --- Dispatch ---
        system.run();
        system.status();

        // --- Scenario 3: Simultaneous multi-floor requests ---
        System.out.println("=== Scenario: Rush Hour ===");
        system.handleExternalRequest(new HallCall(6, Direction.UP));
        system.handleExternalRequest(new HallCall(2, Direction.DOWN));
        system.handleInternalRequest(new CabinRequest(8, 1));
        system.handleInternalRequest(new CabinRequest(0, 2));
        system.run();
        system.status();
    }
}
