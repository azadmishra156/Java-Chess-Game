package Chess;

import java.io.Serializable;

//PAWN
public class Pawn extends Piece implements Serializable {
    public Pawn(boolean isWhite) {
        super(isWhite, 'P');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Piece[][] board) {
        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        // Standard forward move
        if (from.col == to.col) {
            if (to.row == from.row + dir && board[to.row][to.col] == null) {
                return true;
            }

            // Double move from start
            if (from.row == startRow && to.row == from.row + 2 * dir && board[from.row + dir][from.col] == null && board[to.row][to.col] == null) {
                return true;
            }
        }

        // Capture
        if (Math.abs(from.col - to.col) == 1 && to.row == from.row + dir) {
            return board[to.row][to.col] != null && board[to.row][to.col].isWhite != isWhite;
        }

        return false;
    }
}