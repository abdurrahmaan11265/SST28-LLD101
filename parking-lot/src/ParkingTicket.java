import enums.SlotType;
import java.time.LocalDateTime;

public class ParkingTicket {
    private String ticketId;
    private Vehicle vehicle;
    private String slotId;
    private SlotType allocatedSlotType;
    private String gateId;
    private LocalDateTime entryTime;

    public ParkingTicket(String ticketId, Vehicle vehicle, String slotId,
                         SlotType allocatedSlotType, String gateId, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.slotId = slotId;
        this.allocatedSlotType = allocatedSlotType;
        this.gateId = gateId;
        this.entryTime = entryTime;
    }

    public String getTicketId()               { return ticketId; }
    public Vehicle getVehicle()               { return vehicle; }
    public String getSlotId()                 { return slotId; }
    public SlotType getAllocatedSlotType()     { return allocatedSlotType; }
    public String getGateId()                 { return gateId; }
    public LocalDateTime getEntryTime()       { return entryTime; }
}
