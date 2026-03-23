import enums.PlayerStatus;

public class Player {
    private String id;
    private String name;
    private int currentPosition;
    private PlayerStatus status;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.currentPosition = 0;
        this.status = PlayerStatus.PLAYING;
    }

    public void move(int newPosition) {
        this.currentPosition = newPosition;
    }

    public int getPosition() { return currentPosition; }

    public boolean hasWon(int lastCell) {
        return currentPosition == lastCell;
    }

    public void setStatus(PlayerStatus status) { this.status = status; }
    public PlayerStatus getStatus()            { return status; }
    public String getName()                    { return name; }
    public String getId()                      { return id; }
}
