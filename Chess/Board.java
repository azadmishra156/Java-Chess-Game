package Chess;

import java.io.Serializable;

public class Board implements Serializable {
    public Piece[][] board;
    private Position enPassantTarget = null; // For en passant

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
        // ... (no changes here) ...
        // Place black pieces
        board[0][0] = new Rook(false);
        board[0][1] = new Knight(false);
        board[0][2] = new Bishop(false);
        board[0][3] = new Queen(false);
        board[0][4] = new King(false);
        board[0][5] = new Bishop(false);
        board[0][6] = new Knight(false);
        board[0][7] = new Rook(false);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
        }

        // Place white pieces
        board[7][0] = new Rook(true);
        board[7][1] = new Knight(true);
        board[7][2] = new Bishop(true);
        board[7][3] = new Queen(true);
        board[7][4] = new King(true);
        board[7][5] = new Bishop(true);
        board[7][6] = new Knight(true);
        board[7][7] = new Rook(true);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(true);
        }
    }

    // --- Helper Methods ---

    public Piece getPieceAt(Position pos) {
        if (pos.row < 0 || pos.row > 7 || pos.col < 0 || pos.col > 7) {
            return null;
        }
        return board[pos.row][pos.col];
    }
    
    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public boolean isSquareAttacked(Position pos, boolean isAttackerWhite) {
        // ... (no changes here) ...
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece attacker = board[row][col];
                if (attacker != null && attacker.isWhite == isAttackerWhite) {
                    Position from = new Position(row, col);
                    // Use a 'dummy' board for isValidMove's path checking if needed,
                    // but Knight doesn't need path checking.
                    // For R/B/Q, isPathClear is key, and isValidMove handles it.
                    if (attacker instanceof Knight) {
                         if (attacker.isValidMove(from, pos, this)) return true;
                    } 
                    // For sliding pieces, we need to check if the path is clear
                    // *except* for the king on the target square.
                    // A simpler way: King/Pawn checks don't need pathing.
                    else if (attacker instanceof King) {
                        int dx = Math.abs(from.row - pos.row);
                        int dy = Math.abs(from.col - pos.col);
                        if (dx <= 1 && dy <= 1) return true;
                    }
                    else if (attacker instanceof Pawn) {
                         int dir = attacker.isWhite ? -1 : 1;
                         if (Math.abs(from.col - pos.col) == 1 && pos.row == from.row + dir) {
                             return true;
                         }
                    }
                    // For R, B, Q, we must check path clear
                    else if (attacker.isValidMove(from, pos, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean movePiece(Position from, Position to, boolean isWhiteTurn) {
        Piece movingPiece = getPieceAt(from);

        if (movingPiece == null || movingPiece.isWhite != isWhiteTurn) {
            System.out.println("No valid piece at source.");
            return false;
        }

        // --- EN PASSANT FIX ---
        // Store the *current* en passant target. Do NOT reset it yet.
        Position activeEnPassantTarget = enPassantTarget;

        if (!movingPiece.isValidMove(from, to, this)) {
            System.out.println("Invalid move for this piece.");
            return false;
        }

        // --- EN PASSANT FIX ---
        // *NOW* that the move is validated, reset the target for the *next* turn.
        enPassantTarget = null; 

        Piece targetPiece = getPieceAt(to);
        Piece enPassantCapturedPawn = null;
        Position enPassantCapturePos = null;

        // --- Handle En Passant Capture ---
        // Use the *stored* activeEnPassantTarget for the check
        if (movingPiece instanceof Pawn && to.equals(activeEnPassantTarget) && targetPiece == null) {
            int capturedPawnRow = isWhiteTurn ? to.row + 1 : to.row - 1;
            enPassantCapturePos = new Position(capturedPawnRow, to.col);
            enPassantCapturedPawn = board[capturedPawnRow][to.col];
            board[capturedPawnRow][to.col] = null; // Temporarily remove captured pawn
        }

        // --- CASTLING FIX: Simulation ---
        // Store state for castling simulation
        Position rookFrom = null;
        Position rookTo = null;
        Piece castlingRook = null;
        boolean isCastleMove = (movingPiece instanceof King && Math.abs(from.col - to.col) == 2);

        // Make move temporarily
        board[to.row][to.col] = movingPiece;
        board[from.row][from.col] = null;

        if (isCastleMove) {
            int rookCol = (to.col > from.col) ? 7 : 0;
            int newRookCol = (to.col > from.col) ? 5 : 3;
            rookFrom = new Position(from.row, rookCol);
            rookTo = new Position(from.row, newRookCol);
            
            castlingRook = board[rookFrom.row][rookFrom.col];
            board[rookTo.row][rookTo.col] = castlingRook;
            board[rookFrom.row][rookFrom.col] = null;
        }
        // --- End Castling Simulation ---


        // Check if own king is in check
        if (isKingInCheck(isWhiteTurn)) {
            // Undo move
            board[from.row][from.col] = movingPiece;
            board[to.row][to.col] = targetPiece;
            
            // Undo en passant
            if (enPassantCapturedPawn != null) {
                board[enPassantCapturePos.row][enPassantCapturePos.col] = enPassantCapturedPawn;
            }
            
            // --- CASTLING FIX: Undo Simulation ---
            if (isCastleMove && castlingRook != null) {
                board[rookFrom.row][rookFrom.col] = castlingRook;
                board[rookTo.row][rookTo.col] = null;
            }

            // --- EN PASSANT FIX: Restore target if move failed ---
            enPassantTarget = activeEnPassantTarget; 
            
            System.out.println("You can't move into check!");
            return false;
        }
        
        // --- Move is legal, make it permanent ---
        
        // Set 'hasMoved' flag
        movingPiece.setMoved(true);

        // --- Handle Castling Rook Move (This part was already correct) ---
        if (isCastleMove) {
            // We already moved the rook in the simulation, but if we didn't
            // (e.g. if we refactor), this is the permanent move.
            // The simulation logic above is now the *actual* move.
            // Let's just ensure the rook's `hasMoved` flag is set.
            if (castlingRook != null) castlingRook.setMoved(true);
        }

        // --- Set New En Passant Target ---
        if (movingPiece instanceof Pawn && Math.abs(from.row - to.row) == 2) {
            int newTargetRow = isWhiteTurn ? from.row - 1 : from.row + 1;
            enPassantTarget = new Position(newTargetRow, from.col);
        }
        
        return true;
    }

    public void performPromotion(Position pos, String choice, boolean isWhite) {
        // ... (no changes here) ...
        Piece newPiece;
        switch (choice.toUpperCase()) {
            case "R":
            case "ROOK":
                newPiece = new Rook(isWhite);
                break;
            case "B":
            case "BISHOP":
                newPiece = new Bishop(isWhite);
                break;
            case "N":
            case "KNIGHT":
                newPiece = new Knight(isWhite);
                break;
            default:
                newPiece = new Queen(isWhite);
                break;
        }
        board[pos.row][pos.col] = newPiece;
    }

    public void printBoard() {
        // ... (no changes here) ...
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    public boolean isPathClear(Position from, Position to) {
        // ... (no changes here) ...
        int rowDir = Integer.compare(to.row, from.row);
        int colDir = Integer.compare(to.col, from.col);

        int currRow = from.row + rowDir;
        int currCol = from.col + colDir;

        while (currRow != to.row || currCol != to.col) {
            if (board[currRow][currCol] != null) {
                return false;
            }
            currRow += rowDir;
            currCol += colDir;
        }

        return true;
    }

    public boolean isKingInCheck(boolean isWhiteKing) {
        // ... (no changes here) ...
        Position kingPos = null;

        // 1. Find the king on the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board[row][col];
                if (p instanceof King && p.isWhite == isWhiteKing) {
                    kingPos = new Position(row, col);
                    break;
                }
            }
        }

        if (kingPos == null) return false; 

        // 2. Check if any enemy piece can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece attacker = board[row][col];
                if (attacker != null && attacker.isWhite != isWhiteKing) {
                    Position from = new Position(row, col);
                    // Updated to pass 'this' board
                    if (attacker.isValidMove(from, kingPos, this)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isCheckmate(boolean isWhite) {
        if (!isKingInCheck(isWhite)) return false;

        // Try every piece of the current player
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = board[fromRow][fromCol];
                if (piece != null && piece.isWhite == isWhite) {
                    Position from = new Position(fromRow, fromCol);

                    for (int toRow = 0; toRow < 8; toRow++) {
                        for (int toCol = 0; toCol < 8; toCol++) {
                            Position to = new Position(toRow, toCol);
                            
                            // Updated to pass 'this' board
                            if (piece.isValidMove(from, to, this)) {
                                
                                // --- Temporarily store state for undo ---
                                Piece movedPiece = board[fromRow][fromCol];
                                Piece capturedPiece = board[toRow][toCol];
                                boolean oldHasMoved = movedPiece.hasMoved();
                                Position oldEnPassantTarget = getEnPassantTarget();
                                
                                // --- Simulate move (using movePiece for correct simulation) ---
                                // A simpler simulation:
                                board[toRow][toCol] = piece;
                                board[fromRow][fromCol] = null;

                                boolean stillInCheck = isKingInCheck(isWhite);

                                // --- Undo move ---
                                board[fromRow][fromCol] = piece;
                                board[toRow][toCol] = capturedPiece;
                                piece.setMoved(oldHasMoved);
                                enPassantTarget = oldEnPassantTarget; // Restore EP target

                                if (!stillInCheck) {
                                    return false; // At least one move escapes check
                                }
                            }
                        }
                    }
                }
            }
        }

        return true; // No valid moves → checkmate
    }
}