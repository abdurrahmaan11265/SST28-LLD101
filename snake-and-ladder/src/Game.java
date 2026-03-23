import enums.DifficultyLevel;
import enums.GameStatus;
import enums.PlayerStatus;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private List<Player> players;
    private Dice dice;
    private DifficultyLevel difficulty;
    private GameStatus status;
    private int currentPlayerIndex;

    public Game(int numPlayers, int n, DifficultyLevel level) {
        this.board = new Board(n, 0, level);
        this.dice = new Dice();
        this.difficulty = level;
        this.status = GameStatus.NOT_STARTED;
        this.currentPlayerIndex = 0;
        this.players = new ArrayList<>();

        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("P" + i, "Player " + i));
        }
    }

    public void startGame() {
        status = GameStatus.IN_PROGRESS;
        System.out.println("Game started! Board: " + (int) Math.sqrt(board.getSize())
                + "x" + (int) Math.sqrt(board.getSize())
                + " | Difficulty: " + difficulty
                + " | Players: " + players.size());
    }

    public void playTurn() {
        if (status != GameStatus.IN_PROGRESS) return;

        Player player = players.get(currentPlayerIndex);
        if (player.getStatus() != PlayerStatus.PLAYING) {
            advancePlayer();
            return;
        }

        int roll = dice.roll();
        int newPos = player.getPosition() + roll;

        if (board.isValidPosition(newPos)) {
            int resolved = board.resolvePosition(newPos);
            player.move(resolved);

            String event = "";
            if (resolved < newPos) event = " [Snake] -> " + resolved;
            else if (resolved > newPos) event = " [Ladder] -> " + resolved;

            System.out.printf("%s rolled %d: %d -> %d%s%n",
                    player.getName(), roll, newPos, resolved, event);

            if (player.hasWon(board.getSize())) {
                player.setStatus(PlayerStatus.WON);
                System.out.println(player.getName() + " wins!");
                checkAndDeclareWinner();
                advancePlayer();
                return;
            }
        } else {
            System.out.printf("%s rolled %d: stays at %d (overshoot)%n",
                    player.getName(), roll, player.getPosition());
        }

        advancePlayer();
    }

    private void advancePlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void checkAndDeclareWinner() {
        long activePlayers = players.stream()
                .filter(p -> p.getStatus() == PlayerStatus.PLAYING)
                .count();
        if (activePlayers <= 1) {
            status = GameStatus.FINISHED;
            System.out.println("Game over!");
        }
    }

    public List<Player> getActivePlayers() {
        List<Player> active = new ArrayList<>();
        for (Player p : players) {
            if (p.getStatus() == PlayerStatus.PLAYING) active.add(p);
        }
        return active;
    }

    public boolean isGameOver() {
        return status == GameStatus.FINISHED;
    }
}
