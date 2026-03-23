import java.util.Random;

public class Dice {
    private int sides;
    private Random random;

    public Dice() {
        this.sides = 6;
        this.random = new Random();
    }

    public int roll() {
        return random.nextInt(sides) + 1;
    }
}
