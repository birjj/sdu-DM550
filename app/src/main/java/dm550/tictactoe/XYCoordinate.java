package dm550.tictactoe;

public class XYCoordinate implements Coordinate {

    /** variables specifying horizontal position on the board */
    private int x;

    /** variable specifying vertical positoin on the board */
    private int y;

    /** constructor creating a Coordinate from x and y values */
    public XYCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    /**
     * Checks if the coordinate is on a board of size xSize times ySize
     * @param xSize
     * @param ySize
     * @return
     */
    @Override
    public boolean checkBoundaries(int xSize, int ySize) {
        if (this.x < 0 || this.x >= xSize) {
            return false;
        }
        if (this.y < 0 || this.y >= ySize) {
            return false;
        }
        return true;
    }

    /**
     * Returns a new XYCoordinate instance representing this coordinate shifted by dx,dy
     * @param dx
     * @param dy
     * @return new XYCoordinate instance
     */
    @Override
    public Coordinate shift(int dx, int dy) {
        return new XYCoordinate(this.x + dx, this.y + dy);
    }

    @Override
    public String toString() {
        return "XYCoord("+this.x+","+this.y+")";
    }
}
