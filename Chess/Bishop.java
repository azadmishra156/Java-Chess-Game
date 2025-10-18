package Chess;

import java.io.Serializable;

//BISHOP
public class Bishop extends Piece implements Serializable {
    public Bishop(boolean isWhite) {
        super(isWhite, 'B');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        if (Math.abs(from.row - to.row) == Math.abs(from.col - to.col)) {
            // No need to create a new board, just use the one passed in!
            return board.isPathClear(from, to);
        }
        return false;
    }
}