package dm550.tictactoe;

public interface Bot {
    /* gets the bot's ID */
    int getID();

    /* makes a move, returning the coordinate the bot played on */
    Coordinate getMove(TTTBoard board);

    /* returns debug string */
    String debug(TTTBoard board);
}
