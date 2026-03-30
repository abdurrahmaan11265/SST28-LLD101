import enums.Direction;
import java.util.List;

// LOOK algorithm: prefer elevators already moving toward the target floor
public class ScanStrategy implements ElevatorStrategy {

    @Override
    public ElevatorController findBestElevator(int targetFloor, List<ElevatorController> controllers) {
        ElevatorController best = null;
        int minCost = Integer.MAX_VALUE;

        for (ElevatorController controller : controllers) {
            Elevator e = controller.getElevator();
            if (!e.canAccommodate()) continue;

            int cost = computeCost(e, targetFloor);
            if (cost < minCost) {
                minCost = cost;
                best = controller;
            }
        }
        return best;
    }

    // Lower cost = better candidate
    private int computeCost(Elevator e, int targetFloor) {
        int dist = Math.abs(e.getCurrentFloor() - targetFloor);

        if (e.getDirection() == Direction.IDLE) {
            return dist; // idle elevator: pure distance
        }

        boolean movingToward =
            (e.getDirection() == Direction.UP   && targetFloor >= e.getCurrentFloor()) ||
            (e.getDirection() == Direction.DOWN  && targetFloor <= e.getCurrentFloor());

        // Prefer elevators already heading toward the target (half-cost bonus)
        return movingToward ? dist : dist * 2;
    }
}
