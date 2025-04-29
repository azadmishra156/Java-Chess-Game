package Chess;

import java.util.Scanner;

public class ChessGame {
    private Board board;
    private boolean isWhiteTurn;

    public ChessGame() {
        board = new Board();
        isWhiteTurn = true; // White starts
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.printBoard();
            System.out.print((isWhiteTurn ? "White" : "Black") + " to move: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Game ended.");
                break;
            }

            // Expecting input like "e2 e4"
            String[] parts = input.split(" ");
            if (parts.length != 2) {
                System.out.println("Invalid input. Format: e2 e4");
                continue;
            }

            try {
                Position from = Position.fromString(parts[0]);
                Position to = Position.fromString(parts[1]);
            
                boolean moveSuccess = board.movePiece(from, to, isWhiteTurn);
                if (moveSuccess) {
                    board.printBoard();
            
                    if (board.isKingInCheck(!isWhiteTurn)) {
                        System.out.println((!isWhiteTurn ? "White" : "Black") + " king is in check!");

                        if (board.isCheckmate(!isWhiteTurn)) {
                            System.out.println((!isWhiteTurn ? "White" : "Black") + " is checkmated! Game over.");
                            break;
                        }
                        
                    }
            
                    isWhiteTurn = !isWhiteTurn;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid position input.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        game.start();
    }
}

