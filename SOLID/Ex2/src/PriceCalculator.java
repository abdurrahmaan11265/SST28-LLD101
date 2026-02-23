import java.util.*;

public class PriceCalculator {
    public double subtotal(Map<String, MenuItem> menu, List<OrderLine> lines) {
        double total = 0;
        for (OrderLine l : lines) {
            total += menu.get(l.itemId).price * l.qty;
        }
        return total;
    }
}
