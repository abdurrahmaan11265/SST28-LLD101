import enums.SlotType;
import enums.VehicleType;

public class ParkingSlot {
    private String slotId;
    private SlotType slotType;
    private int levelNumber;
    private int distanceFromGate;
    private boolean isOccupied;

    public ParkingSlot(String slotId, SlotType slotType, int levelNumber, int distanceFromGate) {
        this.slotId = slotId;
        this.slotType = slotType;
        this.levelNumber = levelNumber;
        this.distanceFromGate = distanceFromGate;
        this.isOccupied = false;
    }

    public void occupy() { isOccupied = true; }
    public void vacate() { isOccupied = false; }

    public boolean canFit(VehicleType vehicleType) {
        switch (slotType) {
            case SMALL:  return vehicleType == VehicleType.TWO_WHEELER;
            case MEDIUM: return vehicleType == VehicleType.TWO_WHEELER || vehicleType == VehicleType.CAR;
            case LARGE:  return true;
            default:     return false;
        }
    }

    public String getSlotId()           { return slotId; }
    public SlotType getSlotType()       { return slotType; }
    public int getLevelNumber()         { return levelNumber; }
    public int getDistanceFromGate()    { return distanceFromGate; }
    public boolean isOccupied()         { return isOccupied; }
}
