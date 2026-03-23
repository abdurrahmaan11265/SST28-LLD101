import enums.DifficultyLevel;
import java.util.Scanner;

public class SnakeLadderMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter board size n (board will be n x n): ");
        int n = sc.nextInt();

        System.out.print("Enter number of players: ");
        int players = sc.nextInt();

        System.out.print("Enter difficulty (EASY/HARD): ");
        DifficultyLevel level = DifficultyLevel.valueOf(sc.next().toUpperCase());

        sc.close();

        Game game = new Game(players, n, level);
        game.startGame();

        while (!game.isGameOver()) {
            game.playTurn();
        }
    }
}
