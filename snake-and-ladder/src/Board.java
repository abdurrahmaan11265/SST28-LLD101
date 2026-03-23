import enums.DifficultyLevel;
import java.util.*;

public class Board {
    private int size;
    private Cell[] grid;
    private List<Snake> snakes;
    private List<Ladder> ladders;
    private int n;

    public Board(int size, int difficulty, DifficultyLevel level) {
        this.n = size;
        this.size = size * size;
        this.grid = new Cell[this.size + 1];
        this.snakes = new ArrayList<>();
        this.ladders = new ArrayList<>();

        for (int i = 1; i <= this.size; i++) {
            grid[i] = new Cell(i);
        }
        initializeEntities();
    }

    public void initializeEntities() {
        Random random = new Random();
        Set<Integer> entryPoints = new HashSet<>();
        Set<Integer> exitPoints  = new HashSet<>();

        int placed = 0;
        while (placed < n) {
            int head = random.nextInt(size - 2) + 2;
            int tail = random.nextInt(head - 1) + 1;

            if (entryPoints.contains(head) || exitPoints.contains(head)
                    || entryPoints.contains(tail) || exitPoints.contains(tail)) continue;

            entryPoints.add(head);
            exitPoints.add(tail);
            Snake snake = new Snake(head, tail);
            snakes.add(snake);
            grid[head].setEntity(snake);
            placed++;
        }

        placed = 0;
        while (placed < n) {
            int bottom = random.nextInt(size - 2) + 1;
            int top    = random.nextInt(size - bottom) + bottom + 1;

            if (entryPoints.contains(bottom) || exitPoints.contains(bottom)
                    || entryPoints.contains(top) || exitPoints.contains(top)) continue;

            entryPoints.add(bottom);
            exitPoints.add(top);
            Ladder ladder = new Ladder(bottom, top);
            ladders.add(ladder);
            grid[bottom].setEntity(ladder);
            placed++;
        }
    }

    public Cell getCell(int position) {
        return grid[position];
    }

    public int resolvePosition(int pos) {
        if (!isValidPosition(pos)) return pos;
        Cell cell = grid[pos];
        return cell.hasEntity() ? cell.getEntity().getNewPosition() : pos;
    }

    public boolean isValidPosition(int pos) {
        return pos >= 1 && pos <= size;
    }

    public int getSize() { return size; }
}
