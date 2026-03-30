import enums.ElevatorState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Central controller: receives all requests and dispatches elevators
public class ElevatorSystem {
    private final List<ElevatorController> controllers;
    private final List<Floor> floors;
    private final ElevatorStrategy strategy;
    // elevatorId -> controller for fast lookup on cabin requests
    private final Map<Integer, ElevatorController> controllerMap;

    public ElevatorSystem(ElevatorStrategy strategy) {
        this.controllers = new ArrayList<>();
        this.floors = new ArrayList<>();
        this.strategy = strategy;
        this.controllerMap = new HashMap<>();
    }

    public void addElevator(ElevatorController controller) {
        controllers.add(controller);
        controllerMap.put(controller.getElevatorId(), controller);
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    // External request: user on a floor presses hall button
    public void handleExternalRequest(HallCall hallCall) {
        System.out.println("\n>> HallCall: floor=" + hallCall.getFloor()
                + ", direction=" + hallCall.getDirection());

        ElevatorController best = strategy.findBestElevator(hallCall.getFloor(), controllers);
        if (best == null) {
            System.out.println("   No available elevator found.");
            return;
        }

        System.out.println("   Assigned to Elevator " + best.getElevatorId());
        best.addFloorRequest(hallCall.getFloor());

        // Reset the hall button on the floor
        floors.stream()
              .filter(f -> f.getFloorNumber() == hallCall.getFloor())
              .findFirst()
              .ifPresent(f -> {
                  if (hallCall.getDirection() == enums.Direction.UP) f.getUpButton().reset();
                  else f.getDownButton().reset();
              });
    }

    // Internal request: user inside elevator presses destination button
    public void handleInternalRequest(CabinRequest cabinRequest) {
        System.out.println("\n>> CabinRequest: elevator=" + cabinRequest.getElevatorId()
                + ", destination=" + cabinRequest.getDestinationFloor());

        ElevatorController controller = controllerMap.get(cabinRequest.getElevatorId());
        if (controller == null) {
            System.out.println("   Elevator " + cabinRequest.getElevatorId() + " not found.");
            return;
        }
        controller.addFloorRequest(cabinRequest.getDestinationFloor());
    }

    // Run all elevators until all requests are served
    public void run() {
        System.out.println("\n--- Running elevator system ---");
        for (ElevatorController controller : controllers) {
            if (controller.getState() != ElevatorState.IDLE
                    || controller.getElevator().hasPendingRequests()) {
                controller.processAllRequests();
            }
        }
        System.out.println("--- All requests served ---\n");
    }

    public void status() {
        System.out.println("\n=== Elevator Status ===");
        for (ElevatorController c : controllers) {
            Elevator e = c.getElevator();
            System.out.println("  Elevator " + e.getElevatorId()
                    + " | floor=" + e.getCurrentFloor()
                    + " | state=" + e.getState()
                    + " | direction=" + e.getDirection()
                    + " | load=" + e.getCurrentLoad() + "/" + e.getCapacity());
        }
        System.out.println("=======================\n");
    }
}
