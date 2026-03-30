import java.util.List;

public interface ElevatorStrategy {
    ElevatorController findBestElevator(int targetFloor, List<ElevatorController> controllers);
}
