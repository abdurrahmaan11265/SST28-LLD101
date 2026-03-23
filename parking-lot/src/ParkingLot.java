import enums.SlotType;
import enums.VehicleType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ParkingLot {
    private List<Level> levels;
    private List<EntryGate> entryGates;
    private Map<SlotType, Double> hourlyRates;

    private Map<String, ParkingSlot> slotRegistry = new HashMap<>();
    private int ticketCounter = 1;

    public ParkingLot(List<Level> levels, List<EntryGate> entryGates, Map<SlotType, Double> hourlyRates) {
        this.levels = levels;
        this.entryGates = entryGates;
        this.hourlyRates = hourlyRates;

        for (Level level : levels) {
            for (ParkingSlot slot : level.getSlots()) {
                slotRegistry.put(slot.getSlotId(), slot);
            }
        }
    }

    public ParkingTicket park(Vehicle vehicle, LocalDateTime entryTime, SlotType requestedSlotType, String gateId) {
        ParkingSlot slot = findNearestSlot(gateId, vehicle, requestedSlotType);
        if (slot == null) {
            System.out.println("No compatible slot available for " + vehicle.getVehicleNumber());
            return null;
        }

        slot.occupy();
        String ticketId = "TKT-" + ticketCounter++;
        return new ParkingTicket(ticketId, vehicle, slot.getSlotId(), slot.getSlotType(), gateId, entryTime);
    }

    public double exit(ParkingTicket ticket, LocalDateTime exitTime) {
        ParkingSlot slot = slotRegistry.get(ticket.getSlotId());
        if (slot != null) slot.vacate();

        long minutes = ChronoUnit.MINUTES.between(ticket.getEntryTime(), exitTime);
        double hours = Math.ceil(minutes / 60.0);
        double rate = hourlyRates.getOrDefault(ticket.getAllocatedSlotType(), 0.0);
        double bill = hours * rate;

        System.out.printf("Ticket: %s | Vehicle: %s | Slot: %s (%s) | Duration: %.0f hr(s) | Bill: %.2f%n",
                ticket.getTicketId(), ticket.getVehicle().getVehicleNumber(),
                ticket.getSlotId(), ticket.getAllocatedSlotType(), hours, bill);
        return bill;
    }

    public Map<SlotType, Integer> status() {
        Map<SlotType, Integer> available = new LinkedHashMap<>();
        available.put(SlotType.SMALL, 0);
        available.put(SlotType.MEDIUM, 0);
        available.put(SlotType.LARGE, 0);

        for (Level level : levels) {
            for (ParkingSlot slot : level.getSlots()) {
                if (!slot.isOccupied()) {
                    available.merge(slot.getSlotType(), 1, Integer::sum);
                }
            }
        }
        return available;
    }

    public ParkingSlot findNearestSlot(String gateId, Vehicle vehicle, SlotType preferred) {
        EntryGate gate = entryGates.stream()
                .filter(g -> g.getGateId().equals(gateId))
                .findFirst().orElse(null);
        if (gate == null) return null;

        for (SlotType type : upgradeOrder(vehicle.getVehicleType(), preferred)) {
            ParkingSlot nearest = null;
            for (Level level : levels) {
                ParkingSlot candidate = level.getNearestSlot(gate, type);
                if (candidate != null && candidate.canFit(vehicle.getVehicleType())) {
                    if (nearest == null || candidate.getDistanceFromGate() < nearest.getDistanceFromGate()) {
                        nearest = candidate;
                    }
                }
            }
            if (nearest != null) return nearest;
        }
        return null;
    }

    private SlotType[] upgradeOrder(VehicleType vehicleType, SlotType preferred) {
        if (vehicleType == VehicleType.TWO_WHEELER) {
            if (preferred == SlotType.SMALL)  return new SlotType[]{SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE};
            if (preferred == SlotType.MEDIUM) return new SlotType[]{SlotType.MEDIUM, SlotType.LARGE};
            return new SlotType[]{SlotType.LARGE};
        }
        if (vehicleType == VehicleType.CAR) {
            if (preferred == SlotType.MEDIUM) return new SlotType[]{SlotType.MEDIUM, SlotType.LARGE};
            return new SlotType[]{SlotType.LARGE};
        }
        return new SlotType[]{SlotType.LARGE};
    }
}
