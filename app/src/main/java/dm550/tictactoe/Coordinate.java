package dm550.tictactoe;

/** represents a position on a board */
public interface Coordinate {
    
    /** getter for the x value */
    public int getX();

    /** getter for the y value */
    public int getY();
    
    /** check whether this position is valid for the given board size */
    public boolean checkBoundaries(int xSize, int ySize);

    /** move the position by dx to the right and by dy down */
    public Coordinate shift(int dx, int dy);

}

