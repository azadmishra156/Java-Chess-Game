package Chess;

import java.io.Serializable;

//PAWN
public class Pawn extends Piece implements Serializable {
    public Pawn(boolean isWhite) {
        super(isWhite, 'P');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        // Standard forward move
        if (from.col == to.col) {
            // Single move
            if (to.row == from.row + dir && board.getPieceAt(to) == null) {
                return true;
            }
            // Double move from start
            if (from.row == startRow && to.row == from.row + 2 * dir && 
                board.getPieceAt(new Position(from.row + dir, from.col)) == null && 
                board.getPieceAt(to) == null) {
                return true;
            }
        }

        // Capture
        if (Math.abs(from.col - to.col) == 1 && to.row == from.row + dir) {
            // Standard capture
            Piece target = board.getPieceAt(to);
            if (target != null && target.isWhite != isWhite) {
                return true;
            }
            
            // En Passant capture
            if (to.equals(board.getEnPassantTarget())) {
                return true;
            }
        }

        return false;
    }
}