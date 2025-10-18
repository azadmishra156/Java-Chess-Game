package Chess;

import java.io.Serializable;

//QUEEN
public class Queen extends Piece implements Serializable {
    public Queen(boolean isWhite) {
        super(isWhite, 'Q');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        boolean isStraight = (from.row == to.row || from.col == to.col);
        boolean isDiagonal = (Math.abs(from.row - to.row) == Math.abs(from.col - to.col));

        if (isStraight || isDiagonal) {
            // No need to create a new board, just use the one passed in!
            return board.isPathClear(from, to);
        }

        return false;
    }
}