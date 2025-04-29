package Chess;

import java.io.Serializable;

public class Position implements Serializable {
//Represents a square on the chessboard

    public int row, col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static Position fromString(String input) {
        int col = input.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(input.charAt(1));
        return new Position(row, col);
    }

    @Override
    public String toString() {
        return "" + (char) (col + 'a') + (8 - row);
    }
}

