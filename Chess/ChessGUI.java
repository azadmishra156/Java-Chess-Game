package Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ChessGUI extends JFrame {
    private JButton[][] squares = new JButton[8][8];
    private Board board;
    private Position selectedPosition = null;
    private boolean isWhiteTurn = true;
    private java.util.List<Position> validMoves = new ArrayList<>();
    private java.util.Stack<Piece[][]> history = new Stack<>();
    private Theme theme = Theme.CLASSIC;

    public ChessGUI() {
        setTitle("Java Chess");
        setSize(640, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        UIManager.put("Button.focus", Color.BLACK);

        createMenuBar();

        board = new Board();
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Segoe UI", Font.PLAIN, 28));
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                button.setContentAreaFilled(true);
                final int r = row, c = col;
                button.addActionListener(e -> handleClick(r, c));
                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (button.getBackground() != theme.selectedSquare &&
                            button.getBackground() != theme.highlightMove &&
                            button.getBackground() != theme.highlightCapture &&
                            button.getBackground() != theme.kingInCheck) {
                            button.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                        }
                        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }

                    public void mouseExited(MouseEvent e) {
                        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                        button.setCursor(Cursor.getDefaultCursor());
                    }
                });
                squares[row][col] = button;
                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        updateBoard();
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Menu");
        JMenuItem restartItem = new JMenuItem("Restart Game");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        restartItem.addActionListener(e -> restartGame());
        saveItem.addActionListener(e -> saveGame());
        loadItem.addActionListener(e -> loadGame());
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(restartItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.add(exitItem);

        JMenu themeMenu = new JMenu("Theme");
        JMenuItem classicItem = new JMenuItem("Classic");
        JMenuItem darkItem = new JMenuItem("Dark");
        JMenuItem iceItem = new JMenuItem("Ice");

        classicItem.addActionListener(e -> { theme = Theme.CLASSIC; updateBoard(); });
        darkItem.addActionListener(e -> { theme = Theme.DARK; updateBoard(); });
        iceItem.addActionListener(e -> { theme = Theme.ICE; updateBoard(); });

        themeMenu.add(classicItem);
        themeMenu.add(darkItem);
        themeMenu.add(iceItem);

        menuBar.add(gameMenu);
        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
    }

    private void restartGame() {
        board = new Board();
        isWhiteTurn = true;
        history.clear();
        updateBoard();
    }

    private void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("saved_game.ser"))) {
            out.writeObject(board.board);
            out.writeObject(isWhiteTurn);
            JOptionPane.showMessageDialog(this, "Game saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save game.");
        }
    }

    private void loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("saved_game.ser"))) {
            board.board = (Piece[][]) in.readObject();
            isWhiteTurn = (boolean) in.readObject();
            updateBoard();
            JOptionPane.showMessageDialog(this, "Game loaded successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load game.");
        }
    }

    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setBackground((row + col) % 2 == 0 ? theme.lightSquare : theme.darkSquare);
                Piece piece = board.board[row][col];
                JButton button = squares[row][col];

                if (piece != null) {
                    String color = piece.isWhite ? "white" : "black";
                    String name = switch (piece.symbol) {
                        case 'P' -> "pawn";
                        case 'R' -> "rook";
                        case 'N' -> "knight";
                        case 'B' -> "bishop";
                        case 'Q' -> "queen";
                        case 'K' -> "king";
                        default -> "";
                    };
                    String iconName = color + "_" + name;
                    button.setIcon(loadPieceImage(iconName));
                    button.setText("");
                } else {
                    button.setIcon(null);
                    button.setText("");
                }
            }
        }
    }

    private ImageIcon loadPieceImage(String name) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(theme.iconFolder + name + ".png"));
            Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Could not load image: " + theme.iconFolder + name + ".png");
            return null;
        }
    }

    private void handleClick(int row, int col) {
        Position clicked = new Position(row, col);
        if (selectedPosition == null) {
            if (board.board[row][col] != null && board.board[row][col].isWhite == isWhiteTurn) {
                selectedPosition = clicked;
                squares[row][col].setBackground(theme.selectedSquare);
                highlightValidMoves(clicked);
            }
        } else {
            boolean isValidDestination = false;
            for (Position pos : validMoves) {
                if (pos.row == row && pos.col == col) {
                    isValidDestination = true;
                    break;
                }
            }

            if (isValidDestination) {
                Board testBoard = new Board();
                testBoard.board = deepCopyBoard(board.board);
                history.push(deepCopyBoard(board.board));

                boolean testMove = testBoard.movePiece(selectedPosition, clicked, isWhiteTurn);
                if (testMove && !testBoard.isKingInCheck(isWhiteTurn)) {
                    boolean moved = board.movePiece(selectedPosition, clicked, isWhiteTurn);
                    if (moved) {
                        isWhiteTurn = !isWhiteTurn;
                        Piece promoted = board.board[clicked.row][clicked.col];
                        if (promoted instanceof Pawn &&
                            ((promoted.isWhite && clicked.row == 0) || (!promoted.isWhite && clicked.row == 7))) {
                            String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                            String choice = (String) JOptionPane.showInputDialog(
                                this, "Promote pawn to:", "Pawn Promotion",
                                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                            if (choice != null) {
                                boolean isWhite = promoted.isWhite;
                                switch (choice) {
                                    case "Queen" -> board.board[clicked.row][clicked.col] = new Queen(isWhite);
                                    case "Rook" -> board.board[clicked.row][clicked.col] = new Rook(isWhite);
                                    case "Bishop" -> board.board[clicked.row][clicked.col] = new Bishop(isWhite);
                                    case "Knight" -> board.board[clicked.row][clicked.col] = new Knight(isWhite);
                                }
                            }
                        }

                        if (board.isKingInCheck(!isWhiteTurn)) {
                            JOptionPane.showMessageDialog(this,
                                (!isWhiteTurn ? "White" : "Black") + " king is in check!");
                            if (board.isCheckmate(!isWhiteTurn)) {
                                JOptionPane.showMessageDialog(this,
                                    (!isWhiteTurn ? "White" : "Black") + " is checkmated! Game over.");
                                System.exit(0);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Illegal move: King would be in check.");
                }
            }

            resetHighlights();
            selectedPosition = null;
            updateBoard();
            highlightKingInCheck();
        }
    }

    private void highlightValidMoves(Position from) {
        validMoves.clear();
        Piece piece = board.board[from.row][from.col];
        if (piece == null) return;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                if (piece.isValidMove(from, to, board.board)) {
                    Piece target = board.board[to.row][to.col];
                    if (target == null) {
                        squares[row][col].setBackground(theme.highlightMove);
                        validMoves.add(to);
                    } else if (target.isWhite != piece.isWhite) {
                        squares[row][col].setBackground(theme.highlightCapture);
                        validMoves.add(to);
                    }
                }
            }
        }
    }

    private void resetHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? theme.lightSquare : theme.darkSquare);
            }
        }
    }

    private void highlightKingInCheck() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board.board[row][col];
                if (p instanceof King && p.isWhite == isWhiteTurn && board.isKingInCheck(isWhiteTurn)) {
                    squares[row][col].setBackground(theme.kingInCheck);
                    return;
                }
            }
        }
    }

    private Piece[][] deepCopyBoard(Piece[][] original) {
        Piece[][] copy = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = original[row][col];
                if (p == null) continue;
                if (p instanceof Pawn) copy[row][col] = new Pawn(p.isWhite);
                else if (p instanceof Rook) copy[row][col] = new Rook(p.isWhite);
                else if (p instanceof Knight) copy[row][col] = new Knight(p.isWhite);
                else if (p instanceof Bishop) copy[row][col] = new Bishop(p.isWhite);
                else if (p instanceof Queen) copy[row][col] = new Queen(p.isWhite);
                else if (p instanceof King) copy[row][col] = new King(p.isWhite);
            }
        }
        return copy;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }
}
