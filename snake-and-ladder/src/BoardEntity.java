public abstract class BoardEntity {
    protected int startPosition;
    protected int endPosition;

    public BoardEntity(int startPosition, int endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public int getStartPosition() { return startPosition; }
    public int getEndPosition()   { return endPosition; }
    public abstract int getNewPosition();
}
