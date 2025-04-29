package Chess;

import java.io.Serializable;

//Superclass for all pieces
public abstract class Piece implements Serializable {
    public boolean isWhite;
    public char symbol;

    public Piece(boolean isWhite, char symbol) {
        this.isWhite = isWhite;
        this.symbol = symbol;
    }

    public abstract boolean isValidMove(Position from, Position to, Piece[][] board);

    public String toString() {
        return isWhite ? Character.toUpperCase(symbol) + "" : Character.toLowerCase(symbol) + "";
    }
}
