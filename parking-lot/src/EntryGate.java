public class EntryGate {
    private String gateId;
    private int levelNumber;

    public EntryGate(String gateId, int levelNumber) {
        this.gateId = gateId;
        this.levelNumber = levelNumber;
    }

    public String getGateId()   { return gateId; }
    public int getLevelNumber() { return levelNumber; }
}
