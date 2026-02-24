import java.util.*;

public class DeviceRegistry {
    private final java.util.List<Powerable> devices = new ArrayList<>();

    public void add(Powerable d) { devices.add(d); }

    public Powerable getFirstOfType(String simpleName) {
        for (Powerable d : devices) {
            if (d.getClass().getSimpleName().equals(simpleName)) return d;
        }
        throw new IllegalStateException("Missing: " + simpleName);
    }
}
