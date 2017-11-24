package dm550.tictactoe;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TTTBot implements Bot {
    private int ownID;
    public TTTBot(int ID) {
        this.ownID = ID;
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
         * // - Blocks overlap +10 (max 40?)
         * // - Forces next move onto overlap -50
         * - Blocks win +100
         * - Is win +3141592
         * This scoring system is meant for 2-player TTT
         */
        if (board.getPlayer(cell) != 0) {
            return 0;
        }

        int score = 0;
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (i == 0 && j == 0) { continue; }
                // attack lines
                if (this.hasAttackLine(board, cell, i, j)) {
                    score += 1;
                }
            }
        }

        // win/blocks win
        int[] surroundings = this.getSurrounding(board, cell);
        for (int p : surroundings) {
            if (p == this.ownID) {
                // is win
                score += 3141592;
            } else if (p != 0) {
                // blocks win
                score += 100;
            }
        }

        return score;
    }

    /**
     * Checks if a cell has an attack line in a specific direction
     */
    private boolean hasAttackLine(TTTBoard board, Coordinate start, int dx, int dy) {
        int player = board.getPlayer(start);
        for (int i = 1; i < 3; ++i) {
            Coordinate target = start.shift(dx*i, dy*i);
            if (!target.checkBoundaries(board.getSize(), board.getSize())) {
                // abort if out of bounds
                return false;
            }
            int targetPlayer = board.getPlayer(target);
            if (targetPlayer != 0 && targetPlayer != player) {
                // abort if cell owned by other player is found
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a cell is surrounded by cells
     * Each direction (starting from N, going clockwise by 45deg) is represented
     *   by an int representing the player that owns both cells in that direction
     * Finally four of the directions (starting from NE, going clockwise by 45deg)
     *   are represented by an int representing player that owns cells on both sides.
     * Each int is 0 if no player owns both cells
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
                    outp[i++] = 0;
                } else {
                    int closestPlayer = board.getPlayer(closest);
                    int farthestPlayer = board.getPlayer(farthest);
                    int player = 0;
                    if (closestPlayer == farthestPlayer) {
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
                        outp[i++] = 0;
                    } else {
                        int leftPlayer = board.getPlayer(left);
                        int rightPlayer = board.getPlayer(right);
                        int bothSidesPlayer = 0;
                        if (leftPlayer == rightPlayer) {
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
