package Chess;

import java.io.Serializable;

//BISHOP
public class Bishop extends Piece implements Serializable {
    public Bishop(boolean isWhite) {
        super(isWhite, 'B');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Piece[][] board) {
        if (Math.abs(from.row - to.row) == Math.abs(from.col - to.col)) {
            Board b = new Board();
            b.board = board;
            return b.isPathClear(from, to);
        }
        return false;
    }
}
