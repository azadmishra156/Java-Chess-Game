package Chess;

import java.io.Serializable;

//ROOK
public class Rook extends Piece implements Serializable {
    public Rook(boolean isWhite) {
        super(isWhite, 'R');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Piece[][] board) {
        if (from.row == to.row || from.col == to.col) {
            Board b = new Board();
            b.board = board;
            return b.isPathClear(from, to);
        }
        return false;
    }
}