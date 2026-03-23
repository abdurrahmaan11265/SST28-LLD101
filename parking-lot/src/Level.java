import enums.SlotType;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private int levelNumber;
    private List<ParkingSlot> slots;

    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.slots = new ArrayList<>();
    }

    public void addSlot(ParkingSlot slot) {
        slots.add(slot);
    }

    public List<ParkingSlot> getAvailableSlots(SlotType type) {
        List<ParkingSlot> available = new ArrayList<>();
        for (ParkingSlot slot : slots) {
            if (slot.getSlotType() == type && !slot.isOccupied()) {
                available.add(slot);
            }
        }
        return available;
    }

    public ParkingSlot getNearestSlot(EntryGate gate, SlotType type) {
        ParkingSlot nearest = null;
        for (ParkingSlot slot : getAvailableSlots(type)) {
            if (nearest == null || slot.getDistanceFromGate() < nearest.getDistanceFromGate()) {
                nearest = slot;
            }
        }
        return nearest;
    }

    public int getLevelNumber()        { return levelNumber; }
    public List<ParkingSlot> getSlots() { return slots; }
}
