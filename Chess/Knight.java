package Chess;

import java.io.Serializable;

//KNIGHT
public class Knight extends Piece implements Serializable {
    public Knight(boolean isWhite) {
        super(isWhite, 'N');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Piece[][] board) {
        int dx = Math.abs(from.row - to.row);
        int dy = Math.abs(from.col - to.col);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }
}