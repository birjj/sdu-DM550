package dm550.tictactoe;

/** main class creating a board and the GUI
 * defines the game play
 */
public class TTTGame implements Game {

    /** currently active player */
    private int currentPlayer;

    /** total number of players */
    private int numPlayers;

    private int numBots;
    
    /** the board we play on */
    private TTTBoard board;
    
    /** the gui for board games */
    private UserInterface ui;

    /** the bots that are playing */
    private TTTBot[] bots;
    
    /** constructor that gets the number of players */
    public TTTGame(int numPlayers) {
        this.currentPlayer = 1;
        this.numPlayers = numPlayers;
        this.board = new TTTBoard(numPlayers);

        this.bots = new TTTBot[0];
    }
    /** constructor that gets the number of players and bots */
    public TTTGame(int numPlayers, int numBots) {
        this.currentPlayer = 1;
        this.numPlayers = numPlayers + numBots;
        this.numBots = numBots;
        this.board = new TTTBoard(this.numPlayers);

        this.bots = new TTTBot[numBots];
        for (int i = 0; i < numBots; ++i) {
            this.bots[i] = new TTTBot(numPlayers + i + 1);
        }
    }

    @Override
    public String getTitle() {
        return this.numPlayers+"-way Tic Tac Toe";
    }

    @Override
    public void addMove(Coordinate pos) {
        this.board.addMove(pos, this.currentPlayer);
        // if play was by last player
        if (this.currentPlayer == this.numPlayers - this.numBots) {
            for (TTTBot bot : this.bots) {
                Coordinate move = bot.getMove(this.board);
                this.board.addMove(move, bot.getID());
            }
            this.currentPlayer = this.numPlayers;
        }
        if (this.currentPlayer == this.numPlayers) {
            this.currentPlayer = 1;
        } else {
            this.currentPlayer++;
        }
    }

    @Override
    public String getContent(Coordinate pos) {
        String result = "";
        int player = this.board.getPlayer(pos);
        if (player > 0) {
            result += player;
        }
        return result;
    }

    @Override
    public int getHorizontalSize() {
        return this.board.getSize();
    }

    @Override
    public int getVerticalSize() {
        return this.board.getSize();
    }

    @Override
    public void checkResult() {
        int winner = this.board.checkWinning();
        if (winner > 0) {
            this.ui.showResult("Player "+winner+" wins!");
        }
        if (this.board.checkFull()) {
            this.ui.showResult("This is a DRAW!");
        }
    }

    @Override
    public boolean isFree(Coordinate pos) {
        return this.board.isFree(pos);
    }

    @Override
    public void setUserInterface(UserInterface ui) {
        this.ui = ui;
        
    }
    
    public String toString() {
        return "Board before Player "+this.currentPlayer+" of "+this.numPlayers+"'s turn:\n"+this.board.toString();
    }

}
