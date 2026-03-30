import java.util.ArrayList;
import java.util.List;

public class Theater {
    private final String theaterId;
    private final String name;
    private final String city;
    private final String address;
    private final List<Screen> screens;

    public Theater(String theaterId, String name, String city, String address) {
        this.theaterId = theaterId;
        this.name = name;
        this.city = city;
        this.address = address;
        this.screens = new ArrayList<>();
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public String getTheaterId() { return theaterId; }
    public String getName()      { return name; }
    public String getCity()      { return city; }
    public String getAddress()   { return address; }

    @Override
    public String toString() {
        return name + ", " + city;
    }
}
