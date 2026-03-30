import java.util.List;

// Selects the elevator with the shortest distance to the target floor
public class NearestFloorStrategy implements ElevatorStrategy {

    @Override
    public ElevatorController findBestElevator(int targetFloor, List<ElevatorController> controllers) {
        ElevatorController best = null;
        int minCost = Integer.MAX_VALUE;

        for (ElevatorController controller : controllers) {
            if (!controller.getElevator().canAccommodate()) continue;

            int cost = controller.getElevator().estimateCost(targetFloor);
            if (cost < minCost) {
                minCost = cost;
                best = controller;
            }
        }
        return best;
    }
}
