package dm550.tictactoe;

public interface UserInterface {

    /** start the game */
    void startGame(Game game);

    /** show a final result and exit */
    void showResult(String message);

}
