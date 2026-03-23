public class Snake extends BoardEntity {

    public Snake(int head, int tail) {
        super(head, tail);
    }

    @Override
    public int getNewPosition()  { return endPosition; }
    public int getHeadPosition() { return startPosition; }
    public int getTailPosition() { return endPosition; }
}
