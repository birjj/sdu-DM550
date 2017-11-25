package dm550.tictactoe;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TTTBot implements Bot {
    private int ownID;
    private int numPlayers;
    public TTTBot(int ID, int numPlayers) {
        this.ownID = ID;
        this.numPlayers = numPlayers;
    }

    @Override
    public int getID() {
        return this.ownID;
    }

    @Override
    public Coordinate getMove(TTTBoard board) {
        int size = board.getSize();
        Coordinate bestMove = new XYCoordinate(0,0);
        int bestScore = -1;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                Coordinate coord = new XYCoordinate(x, y);
                if (board.getPlayer(coord) != 0) { continue; }

                int score = this.getScore(board, coord);
                if (score > bestScore) {
                    bestMove = coord;
                    bestScore = score;
                }
            }
        }
        return bestMove;
    }

    /**
     * Gets the predicted move score for a cell on board
     */
    private int getScore(TTTBoard board, Coordinate cell) {
        /**
         * Cells are valued as such:
         * - For each possible attack line +1 (max 8)
         * - Blocks win +100
         *     But -10 for number of players between us and blocked
         * - Is win +3141592
         * This scoring system is meant for 2-player TTT
         */
        if (board.getPlayer(cell) != 0) {
            return 0;
        }

        int score = 0;

        // attack lines
        int[] surroundings = this.getSurrounding(board, cell);
        score += this.numAttackLines(board, cell, surroundings);

        // win/blocks win
        for (int p : surroundings) {
            if (p == this.ownID) {
                // is win
                score += 3141592;
            } else if (p > 0) {
                // blocks win
                score += 100;

                // discount block by how many players are between us
                score -= ((p - this.ownID - 1 + this.numPlayers) % this.numPlayers) * 10;
            }
        }

        return score;
    }

    /**
     * Gets the number of attack lines from a cell
     */
    private int numAttackLines(TTTBoard board, Coordinate cell, int[] surroundings) {
        int player = this.ownID;
        int outp = 0;
        for (int p : surroundings) {
            if (p == 0 || p == player || p == -player) {
                ++outp;
            }
        }

        return outp;
    }

    /**
     * Checks if a cell is surrounded by cells
     * Each direction (starting from N, going clockwise by 45deg) is represented
     *   by an int representing the player that owns both cells in that direction
     * Finally four of the directions (starting from NE, going clockwise by 45deg)
     *   are represented by an int representing player that owns cells on both sides.
     * Int is 0 if both are empty.
     * Int is -[playerID] if a player owns 1 and the others are empty
     * Int is -[MIN_VAL] if cells are owned by different players
     * @returns An int[] containing each number
     */
    private int[] getSurrounding(TTTBoard board, Coordinate start) {
        int[] outp = new int[8 + 4];
        int i = 0;
        int size = board.getSize();

        // each direction
        for (int dx = -1; dx < 2; ++dx) {
            for (int dy = -1; dy < 2; ++dy) {
                if (dx == 0 && dy == 0) { continue; }

                // check both in that direction
                Coordinate closest = start.shift(dx, dy);
                Coordinate farthest = start.shift(dx*2, dy*2);
                if (!farthest.checkBoundaries(size, size)) {
                    // no surrounding if went out of bounds
                    outp[i++] = -Integer.MIN_VALUE;
                } else {
                    int closestPlayer = board.getPlayer(closest);
                    int farthestPlayer = board.getPlayer(farthest);
                    int player = -Integer.MIN_VALUE;
                    if (closestPlayer == 0) {
                        player = -farthestPlayer;
                    } else if (farthestPlayer == 0) {
                        player = -closestPlayer;
                    } else if (closestPlayer == farthestPlayer) {
                        player = closestPlayer;
                    }
                    outp[i++] = player;
                }

                // check each side in 4 of the directions
                if (dx > -1 && !(dy == -1 && dx == 0)) {
                    Coordinate left = start.shift(-dx, -dy);
                    Coordinate right = start.shift(dx, dy);
                    if (!left.checkBoundaries(size, size)
                            || !right.checkBoundaries(size, size)) {
                        outp[i++] = -Integer.MIN_VALUE;
                    } else {
                        int leftPlayer = board.getPlayer(left);
                        int rightPlayer = board.getPlayer(right);
                        int bothSidesPlayer = -Integer.MIN_VALUE;
                        if (leftPlayer == 0) {
                            bothSidesPlayer = -rightPlayer;
                        } else if (rightPlayer == 0) {
                            bothSidesPlayer = -leftPlayer;
                        } else if (leftPlayer == rightPlayer) {
                            bothSidesPlayer = leftPlayer;
                        }
                        outp[i++] = bothSidesPlayer;
                    }
                }
            }
        }

        return outp;
    }


    /**
     * Returns debug string
     */
    @Override
    public String debug(TTTBoard board) {
        int size = board.getSize();
        StringBuilder outp = new StringBuilder("Bot "+this.ownID+"\n");
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                Coordinate coord = new XYCoordinate(x,y);
                int player = board.getPlayer(coord);
                if (player == 0) {
                    outp.append("[" + this.getScore(board, coord) + "]");
                } else {
                    outp.append(" "+player+" ");
                }
            }
            outp.append("\n");
        }
        outp.append("\n");
        return outp.toString();
    }
}
