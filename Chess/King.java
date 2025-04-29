package Chess;

import java.io.Serializable;

//KING
public class King extends Piece implements Serializable {
    public King(boolean isWhite) {
        super(isWhite, 'K');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Piece[][] board) {
        int dx = Math.abs(from.row - to.row);
        int dy = Math.abs(from.col - to.col);
        return dx <= 1 && dy <= 1;
    }
}