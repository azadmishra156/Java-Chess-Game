package Chess;

import java.io.Serializable;

//ROOK
public class Rook extends Piece implements Serializable {
    public Rook(boolean isWhite) {
        super(isWhite, 'R');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        if (from.row == to.row || from.col == to.col) {
            // No need to create a new board, just use the one passed in!
            return board.isPathClear(from, to);
        }
        return false;
    }
}