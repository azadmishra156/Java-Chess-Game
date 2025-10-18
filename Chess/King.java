package Chess;

import java.io.Serializable;

//KING
public class King extends Piece implements Serializable {
    public King(boolean isWhite) {
        super(isWhite, 'K');
    }

    @Override
    public boolean isValidMove(Position from, Position to, Board board) {
        int dx = Math.abs(from.row - to.row);
        int dy = Math.abs(from.col - to.col);

        // Standard move
        if (dx <= 1 && dy <= 1) {
            return true;
        }

        // --- Castling Logic ---
        if (!hasMoved && dx == 0 && dy == 2 && from.row == to.row) {
            
            // Can't castle if in check
            if (board.isKingInCheck(isWhite)) {
                return false;
            }

            // Determine if kingside or queenside
            int rookCol = (to.col > from.col) ? 7 : 0;
            Position rookPos = new Position(from.row, rookCol);
            Piece rook = board.getPieceAt(rookPos);

            // Check if rook exists, is a Rook, and hasn't moved
            if (rook == null || !(rook instanceof Rook) || rook.hasMoved()) {
                return false;
            }

            // Check if path is clear between king and rook
            if (!board.isPathClear(from, rookPos)) {
                return false;
            }

            // Check if king passes through or lands on an attacked square
            int step = (to.col > from.col) ? 1 : -1;
            Position p1 = new Position(from.row, from.col + step);
            Position p2 = new Position(from.row, from.col + 2 * step);

            if (board.isSquareAttacked(p1, !isWhite) || board.isSquareAttacked(p2, !isWhite)) {
                return false;
            }
            
            return true; // Castling is valid
        }

        return false;
    }
}