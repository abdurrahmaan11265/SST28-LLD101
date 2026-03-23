public class Ladder extends BoardEntity {

    public Ladder(int bottom, int top) {
        super(bottom, top);
    }

    @Override
    public int getNewPosition()    { return endPosition; }
    public int getBottomPosition() { return startPosition; }
    public int getTopPosition()    { return endPosition; }
}
