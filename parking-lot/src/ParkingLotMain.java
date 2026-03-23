import enums.SlotType;
import enums.VehicleType;
import java.time.LocalDateTime;
import java.util.*;

public class ParkingLotMain {
    public static void main(String[] args) {
        Level level1 = new Level(1);
        Level level2 = new Level(2);

        for (int i = 0; i < 5; i++) {
            level1.addSlot(new ParkingSlot("L1-S" + i, SlotType.SMALL,  1, i));
            level1.addSlot(new ParkingSlot("L1-M" + i, SlotType.MEDIUM, 1, i));
            level1.addSlot(new ParkingSlot("L1-L" + i, SlotType.LARGE,  1, i));
            level2.addSlot(new ParkingSlot("L2-S" + i, SlotType.SMALL,  2, i));
            level2.addSlot(new ParkingSlot("L2-M" + i, SlotType.MEDIUM, 2, i));
            level2.addSlot(new ParkingSlot("L2-L" + i, SlotType.LARGE,  2, i));
        }

        List<Level> levels = Arrays.asList(level1, level2);
        List<EntryGate> gates = Arrays.asList(
                new EntryGate("G1", 1),
                new EntryGate("G2", 2)
        );

        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL,  20.0);
        rates.put(SlotType.MEDIUM, 40.0);
        rates.put(SlotType.LARGE,  80.0);

        ParkingLot lot = new ParkingLot(levels, gates, rates);

        System.out.println("=== Initial Status ===");
        lot.status().forEach((type, count) -> System.out.println(type + ": " + count));

        LocalDateTime entry = LocalDateTime.of(2026, 3, 23, 10, 0);

        ParkingTicket t1 = lot.park(new Vehicle("KA01AB1234", VehicleType.TWO_WHEELER), entry, SlotType.SMALL, "G1");
        ParkingTicket t2 = lot.park(new Vehicle("KA02CD5678", VehicleType.CAR),         entry, SlotType.MEDIUM, "G1");
        ParkingTicket t3 = lot.park(new Vehicle("KA03EF9012", VehicleType.BUS),         entry, SlotType.LARGE, "G2");

        System.out.println("\n=== Status After Parking ===");
        lot.status().forEach((type, count) -> System.out.println(type + ": " + count));

        System.out.println("\n=== Exit ===");
        LocalDateTime exit = LocalDateTime.of(2026, 3, 23, 13, 30);
        lot.exit(t1, exit);
        lot.exit(t2, exit);
        lot.exit(t3, exit);

        System.out.println("\n=== Status After Exit ===");
        lot.status().forEach((type, count) -> System.out.println(type + ": " + count));
    }
}
