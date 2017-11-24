package dm550.tictactoe;

import java.util.*;

public class CLI implements UserInterface {

    public static void main(String[] args) {
        UserInterface ui = new CLI();
        int numPlayers = getParameter("number of players",2,6);
        Game game = new TTTGame(numPlayers);
        ui.startGame(game);

    }

    private static int getParameter(String message, int min, int max) {
        int result = min-1;
        while (result < min || result > max) {
            System.out.print("Please enter "+message+" between "+min+" and "+max+": ");
            try {
                result = Integer.parseInt(new Scanner(System.in).nextLine());
            } catch (NumberFormatException e) {}
        }
        return result;
    }

    @Override
    public void showResult(String message) {
        System.out.println(message+"\n\nThanks for playing.");
        System.exit(0);
    }

    @Override
    public void startGame(Game game) {
        game.setUserInterface(this);
        while (true) {
            System.out.println(game);
            game.checkResult();
            Coordinate pos = null;
            while (true) {
                int x = this.getParameter("x coordinate", 1, game.getHorizontalSize())-1;
                int y = this.getParameter("y coordinate", 1, game.getVerticalSize())-1;
                pos = new XYCoordinate(x,y);
                if (game.isFree(pos)) {
                    break;
                }
                System.out.println("The position is not free!");
            }
            game.addMove(pos);
        }
    }

}
