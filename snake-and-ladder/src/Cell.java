public class Cell {
    private int position;
    private BoardEntity entity;

    public Cell(int position) {
        this.position = position;
    }

    public int getPosition()              { return position; }
    public boolean hasEntity()            { return entity != null; }
    public BoardEntity getEntity()        { return entity; }
    public void setEntity(BoardEntity e)  { this.entity = e; }
}
