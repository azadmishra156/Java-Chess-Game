package Chess;

import java.io.Serializable;

//Superclass for all pieces
public abstract class Piece implements Serializable {
    public boolean isWhite;
    public char symbol;
    protected boolean hasMoved; // Added for castling

    public Piece(boolean isWhite, char symbol) {
        this.isWhite = isWhite;
        this.symbol = symbol;
        this.hasMoved = false; // All pieces start as not moved
    }

    /**
     * Updated signature: Now takes the full Board object.
     * This allows a piece to check game state, like en passant targets or if the king is in check.
     */
    public abstract boolean isValidMove(Position from, Position to, Board board);

    public String toString() {
        return isWhite ? Character.toUpperCase(symbol) + "" : Character.toLowerCase(symbol) + "";
    }
    
    // Getters and setters for hasMoved
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}