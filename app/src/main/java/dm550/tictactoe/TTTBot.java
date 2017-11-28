package dm550.tictactoe;

import android.util.SparseIntArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
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
         * - Forcing onto an overlap -20
         * - Is win +3141592
         * This scoring system is meant for 2-player TTT
         */
        if (board.getPlayer(cell) != 0) {
            return -Integer.MIN_VALUE;
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

        // avoid forcing onto overlap
        Coordinate forcedLocation = this.getForcedLocation(board, cell);
        if (forcedLocation != null) {
            boolean isOverlap = this.isOverlapped(board, forcedLocation);
            if (isOverlap) {
                score -= 20;
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
     * Gets the location we are forcing an enemy to play on (otherwise we win)
     * if we place on move
     */
    private Coordinate getForcedLocation(final TTTBoard board, Coordinate move) {
        final Coordinate[] outp = new Coordinate[1]; // can't use just Coordinate because we assign to it later...
        final int size = board.getSize();
        final TTTBot self = this;
        SurroundingReceiver receiver = new SurroundingReceiver() {
            @Override
            public void onSurrounding(Coordinate c1, Coordinate c2, int index) {
                if (outp[0] != null) {
                    return;
                }
                if (!c1.checkBoundaries(size, size)
                        || !c2.checkBoundaries(size, size)) {
                    return;
                }

                int p1 = board.getPlayer(c1);
                int p2 = board.getPlayer(c2);
                if (p1 == self.ownID && p2 == 0) {
                    outp[0] = c2;
                } else if (p1 == 0 && p2 == self.ownID) {
                    outp[0] = c1;
                }
            }
        };
        this.forEachSurrounding(receiver, board, move);

        return outp[0];
    }

    /**
     * Checks if a field is covered by multiple attack lines by a single enemy
     */
    private boolean isOverlapped(final TTTBoard board, Coordinate cell) {
        final SparseIntArray playerAttackLines = new SparseIntArray();
        final int size = board.getSize();
        SurroundingReceiver receiver = new SurroundingReceiver() {
            @Override
            public void onSurrounding(Coordinate c1, Coordinate c2, int index) {
                if (!c1.checkBoundaries(size, size)
                        || !c2.checkBoundaries(size, size)) {
                    return;
                }

                int p1 = board.getPlayer(c1);
                int p2 = board.getPlayer(c2);
                int player;
                if (p1 != 0 && p2 == 0) {
                    player = p1;
                } else if (p1 == 0 && p2 != 0) {
                    player = p2;
                } else if (p1 == p2 && p1 != 0) {
                    player = p1;
                } else {
                    return;
                }
                playerAttackLines.put(player, playerAttackLines.get(player) + 1);
            }
        };
        this.forEachSurrounding(receiver, board, cell);

        for (int i = 0; i < playerAttackLines.size(); ++i) {
            int key = playerAttackLines.keyAt(i);
            if (playerAttackLines.get(key) >= 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a cell is surrounded by cells. Each direction (see .forEachSurrounding)
     * is represented in the output array by an integer.
     * Int is 0 if both are empty.
     * Int is -[playerID] if a player owns 1 and the others are empty
     * Int is -[MIN_VAL] if cells are owned by different players
     * @returns An int[] containing each number
     */
    private int[] getSurrounding(final TTTBoard board, Coordinate start) {
        final int[] outp = new int[8 + 4];
        final int size = board.getSize();

        SurroundingReceiver receiver = new SurroundingReceiver() {
            @Override
            public void onSurrounding(Coordinate c1, Coordinate c2, int index) {
                if (!c1.checkBoundaries(size, size)
                        || !c2.checkBoundaries(size, size)) {
                    outp[index] = -Integer.MAX_VALUE;
                } else {
                    int p1 = board.getPlayer(c1);
                    int p2 = board.getPlayer(c2);
                    int player = -Integer.MIN_VALUE;
                    if (p1 == 0) {
                        player = -p2;
                    } else if (p2 == 0) {
                        player = -p1;
                    } else if (p1 == p2) {
                        player = p1;
                    }
                    outp[index] = player;
                }

            }
        };

        this.forEachSurrounding(
                receiver,
                board,
                start
        );

        return outp;
    }

    private interface SurroundingReceiver {
        public void onSurrounding(Coordinate c1, Coordinate c2, int index);
    }

    /**
     * Calls onSurrounding once for each direction (8 times in 45deg intervals clockwise from NW)
     * and once for each centered direction (4 times in 45deg intervals clockwise from NE)
     */
    private void forEachSurrounding(SurroundingReceiver receiver, TTTBoard board, Coordinate start) {
        int index = 0;

        // each direction
        for (int dx = -1; dx < 2; ++dx) {
            for (int dy = -1; dy < 2; ++dy) {
                if (dx == 0 && dy == 0) { continue; }

                // check both in that direction
                Coordinate closest = start.shift(dx, dy);
                Coordinate farthest = start.shift(dx*2, dy*2);

                receiver.onSurrounding(closest, farthest, index++);

                // check each side in 4 of the directions
                if (dx > -1 && !(dy == -1 && dx == 0)) {
                    Coordinate left = start.shift(-dx, -dy);
                    Coordinate right = start.shift(dx, dy);

                    receiver.onSurrounding(left, right, index++);
                }
            }
        }
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
