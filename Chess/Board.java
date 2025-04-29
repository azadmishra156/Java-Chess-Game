package Chess;

import java.io.Serializable;

import javax.swing.JOptionPane;

public class Board implements Serializable {
    public Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {
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

        // All other positions are null (empty)
    }

    public boolean movePiece(Position from, Position to, boolean isWhiteTurn) {
        Piece movingPiece = board[from.row][from.col];
    
        if (movingPiece == null || movingPiece.isWhite != isWhiteTurn) {
            System.out.println("No valid piece at source.");
            return false;
        }
    
        if (!movingPiece.isValidMove(from, to, board)) {
            System.out.println("Invalid move for this piece.");
            return false;
        }
    
        Piece targetPiece = board[to.row][to.col];
    
        // Make move temporarily
        board[to.row][to.col] = movingPiece;
        board[from.row][from.col] = null;
    
        // Check if own king is in check
        if (isKingInCheck(isWhiteTurn)) {
            // Undo move
            board[from.row][from.col] = movingPiece;
            board[to.row][to.col] = targetPiece;
    
            System.out.println("You can't move into check!");
            return false;
        }

        // Check for pawn promotion
        if (movingPiece instanceof Pawn) {
            if ((movingPiece.isWhite && to.row == 0) || (!movingPiece.isWhite && to.row == 7)) {
                promotePawn(to, movingPiece.isWhite);
            }
}


    
        return true;
    }
    

    public void printBoard() {
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

    // Check if path between from and to is clear (excluding knights)
public boolean isPathClear(Position from, Position to) {
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

    if (kingPos == null) return false; // King not found (shouldn’t happen)

    // 2. Check if any enemy piece can attack the king
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            Piece attacker = board[row][col];
            if (attacker != null && attacker.isWhite != isWhiteKing) {
                Position from = new Position(row, col);
                if (attacker.isValidMove(from, kingPos, board)) {
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
                        Piece target = board[toRow][toCol];

                        // Simulate legal move
                        if (piece.isValidMove(from, to, board)) {
                            board[toRow][toCol] = piece;
                            board[fromRow][fromCol] = null;

                            boolean stillInCheck = isKingInCheck(isWhite);

                            // Undo move
                            board[fromRow][fromCol] = piece;
                            board[toRow][toCol] = target;

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



private void promotePawn(Position pos, boolean isWhite) {
    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
    String choice = (String) JOptionPane.showInputDialog(
        null,
        "Promote pawn to:",
        "Pawn Promotion",
        JOptionPane.PLAIN_MESSAGE,
        null,
        options,
        "Queen"
    );

    switch (choice) {
        case "Rook":
            board[pos.row][pos.col] = new Rook(isWhite); break;
        case "Bishop":
            board[pos.row][pos.col] = new Bishop(isWhite); break;
        case "Knight":
            board[pos.row][pos.col] = new Knight(isWhite); break;
        default:
            board[pos.row][pos.col] = new Queen(isWhite); break;
    }
}



}