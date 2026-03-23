import enums.InkType;

public class Main {
    public static void main(String[] args) {
        Refill blueRefill = new Refill(InkType.BLUE, 100);
        Pen ballpoint = new BallpointPen(blueRefill);
        ballpoint.start();
        ballpoint.write("Hello from BallpointPen!");
        ballpoint.close();

        Refill blackRefill = new Refill(InkType.BLACK, 50);
        Pen fountain = new FountainPen(blackRefill);
        fountain.start();
        fountain.write("Hello from FountainPen!");
        fountain.close();

        Refill redRefill = new Refill(InkType.RED, 30);
        Pen gel = new GelPen(redRefill);
        gel.start();
        gel.write("Hello from GelPen!");
        gel.refill();
        gel.start();
        gel.write("Writing again after refill!");
        gel.close();
    }
}
