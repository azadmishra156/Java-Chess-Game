package Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;

public class ChessGUI extends JFrame {
    // ... (no changes to fields) ...
    private JButton[][] squares = new JButton[8][8];
    private Board board;
    private Position selectedPosition = null;
    private boolean isWhiteTurn = true;
    private java.util.List<Position> validMoves = new ArrayList<>();
    private java.util.Stack<Piece[][]> history = new Stack<>();
    private Theme theme = Theme.CLASSIC;
    private JLayeredPane layeredPane;
    private JPanel boardPanel;

    public ChessGUI() {
        // ... (no changes to constructor) ...
        setTitle("Java Chess");
        setSize(640, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        // Enable layered pane for animations
        layeredPane = getLayeredPane();
        
        UIManager.put("Button.focus", Color.BLACK);

        createMenuBar();
        initializeBoard();
        setVisible(true);
    }

    private void initializeBoard() {
        // ... (no changes here) ...
        board = new Board();
        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBounds(0, 0, 640, 640);
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = createSquareButton(row, col);
                squares[row][col] = button;
                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        updateBoard();
    }

    private JButton createSquareButton(int row, int col) {
        // ... (no changes here) ...
        JButton button = new JButton();
        button.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setContentAreaFilled(true);
        
        button.addActionListener(e -> handleClick(row, col));
        
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
        
        return button;
    }

    private void createMenuBar() {
        // ... (no changes here) ...
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
        // ... (no changes here) ...
        board = new Board();
        isWhiteTurn = true;
        history.clear();
        updateBoard();
    }

    private void saveGame() {
        // ... (no changes here) ...
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("saved_game.ser"))) {
            out.writeObject(board); // Save the whole Board object
            out.writeObject(isWhiteTurn);
            JOptionPane.showMessageDialog(this, "Game saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save game.");
        }
    }

    private void loadGame() {
        // ... (no changes here) ...
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("saved_game.ser"))) {
            board = (Board) in.readObject(); // Load the whole Board object
            isWhiteTurn = (boolean) in.readObject();
            updateBoard();
            JOptionPane.showMessageDialog(this, "Game loaded successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load game.");
        }
    }

    private void updateBoard() {
        // ... (no changes here) ...
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].setBackground((row + col) % 2 == 0 ? theme.lightSquare : theme.darkSquare);
                Piece piece = board.getPieceAt(new Position(row, col));
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
        highlightKingInCheck();
    }

    private ImageIcon loadPieceImage(String name) {
        // ... (no changes here) ...
        try {
            java.net.URL url = getClass().getResource(theme.iconFolder + name + ".png");
            if (url == null) {
                System.err.println("Could not find image: " + theme.iconFolder + name + ".png");
                return null;
            }
            ImageIcon icon = new ImageIcon(url);
            Image scaledImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Could not load image: " + theme.iconFolder + name + ".png");
            return null;
        }
    }

    private void handleClick(int row, int col) {
        // ... (no changes here) ...
        Position clicked = new Position(row, col);
        if (selectedPosition == null) {
            Piece p = board.getPieceAt(clicked);
            if (p != null && p.isWhite == isWhiteTurn) {
                selectedPosition = clicked;
                squares[row][col].setBackground(theme.selectedSquare);
                highlightValidMoves(clicked);
            }
        } else {
            boolean isValidDestination = validMoves.stream().anyMatch(pos -> pos.row == row && pos.col == col);

            if (isValidDestination) {
                animatePieceMove(selectedPosition, clicked);
                
            } else {
                resetHighlights();
                selectedPosition = null;
                updateBoard(); // Deselect
            }
        }
    }


    private void animatePieceMove(Position from, Position to) {
        // ... (no changes here) ...
        JButton fromButton = squares[from.row][from.col];
        JButton toButton = squares[to.row][to.col];
        Icon pieceIcon = fromButton.getIcon();
        fromButton.setIcon(null);

        Point fromPoint = fromButton.getLocation();
        Point toPoint = toButton.getLocation();
        int deltaX = toPoint.x - fromPoint.x;
        int deltaY = toPoint.y - fromPoint.y;
        
        JLabel movingPiece = new JLabel(pieceIcon);
        movingPiece.setSize(fromButton.getWidth(), fromButton.getHeight());
        movingPiece.setLocation(fromPoint);
        layeredPane.add(movingPiece, JLayeredPane.DRAG_LAYER);
        layeredPane.moveToFront(movingPiece);
        
        int duration = 200;
        int frames = 20;
        int delay = duration / frames;
        final int[] frame = {0};
        
        Timer timer = new Timer(delay, e -> {
            frame[0]++;
            
            float progress = (float) frame[0] / frames;
            progress = (float) Math.sin(progress * Math.PI / 2);
            int currentX = fromPoint.x + (int)(deltaX * progress);
            int currentY = fromPoint.y + (int)(deltaY * progress);
            
            movingPiece.setLocation(currentX, currentY);
            
            if (frame[0] >= frames) {
                ((Timer)e.getSource()).stop();
                layeredPane.remove(movingPiece);
                toButton.setIcon(pieceIcon); // Temporarily set icon
                toButton.repaint();
                
                SwingUtilities.invokeLater(() -> {
                    // Now, make the actual move on the board
                    boolean moved = board.movePiece(selectedPosition, to, isWhiteTurn);
                    if (moved) {
                        handlePostMove(to);
                    } else {
                        // Move was illegal (e.g., put king in check)
                        JOptionPane.showMessageDialog(this, "Illegal move: King would be in check.");
                        // Revert visual change
                        fromButton.setIcon(pieceIcon);
                        // Clear the 'to' square icon, which might have been a capture
                        Piece targetPiece = board.getPieceAt(to);
                        if (targetPiece == null) {
                            toButton.setIcon(null);
                        } // if targetPiece != null, updateBoard() will fix it
                        
                        resetHighlights();
                        selectedPosition = null;
                        updateBoard(); // Fully redraw the board to fix any visual glitch
                    }
                });
            }
        });
        
        timer.setInitialDelay(0);
        timer.start();
    }

    private void handlePostMove(Position to) {
        // ... (no changes here) ...
        isWhiteTurn = !isWhiteTurn;
        
        // --- Handle Pawn Promotion ---
        Piece promoted = board.getPieceAt(to);
        if (promoted instanceof Pawn && 
            ((promoted.isWhite && to.row == 0) || (!promoted.isWhite && to.row == 7))) {
            handlePromotion(to, promoted.isWhite);
        }

        // --- Handle Check / Checkmate ---
        if (board.isKingInCheck(!isWhiteTurn)) {
            JOptionPane.showMessageDialog(this,
                (!isWhiteTurn ? "White" : "Black") + " king is in check!");
            if (board.isCheckmate(!isWhiteTurn)) {
                JOptionPane.showMessageDialog(this,
                    (!isWhiteTurn ? "White" : "Black") + " is checkmated! Game over.");
                restartGame(); // Or some other end-game logic
            }
        }

        resetHighlights();
        selectedPosition = null;
        updateBoard();
    }

    private void handlePromotion(Position pos, boolean isWhite) {
        // ... (no changes here) ...
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        String choice = (String) JOptionPane.showInputDialog(
            this, "Promote pawn to:", "Pawn Promotion",
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            
        if (choice == null) {
            choice = "Queen"; // Default to Queen if dialog is cancelled
        }
        
        // Use the new board method to perform the promotion
        board.performPromotion(pos, choice, isWhite);
        
        updateBoard(); // Redraw board with the new piece
    }

    private void highlightValidMoves(Position from) {
        validMoves.clear();
        Piece piece = board.getPieceAt(from);
        if (piece == null) return;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position to = new Position(row, col);
                
                // Updated to pass 'board'
                if (piece.isValidMove(from, to, board)) {
                    
                    // --- CASTLING FIX: Simulation ---
                    Piece target = board.getPieceAt(to);
                    board.board[to.row][to.col] = piece; // Simulate
                    board.board[from.row][from.col] = null;
                    
                    Position rookFrom = null;
                    Position rookTo = null;
                    Piece castlingRook = null;
                    boolean isCastleMove = (piece instanceof King && Math.abs(from.col - to.col) == 2);
                    
                    if (isCastleMove) {
                        int rookCol = (to.col > from.col) ? 7 : 0;
                        int newRookCol = (to.col > from.col) ? 5 : 3;
                        rookFrom = new Position(from.row, rookCol);
                        rookTo = new Position(from.row, newRookCol);
                        
                        castlingRook = board.board[rookFrom.row][rookFrom.col];
                        board.board[rookTo.row][rookTo.col] = castlingRook;
                        board.board[rookFrom.row][rookFrom.col] = null;
                    }
                    
                    boolean selfCheck = board.isKingInCheck(piece.isWhite);
                    
                    // Undo simulation
                    board.board[from.row][from.col] = piece;
                    board.board[to.row][to.col] = target;
                    
                    if (isCastleMove && castlingRook != null) {
                        board.board[rookFrom.row][rookFrom.col] = castlingRook;
                        board.board[rookTo.row][rookTo.col] = null;
                    }
                    // --- End Simulation ---

                    if (!selfCheck) {
                        Piece targetPiece = board.getPieceAt(to); // Re-get target just in case
                        if (targetPiece == null) {
                            squares[row][col].setBackground(theme.highlightMove);
                            validMoves.add(to);
                        } else if (targetPiece.isWhite != piece.isWhite) {
                            squares[row][col].setBackground(theme.highlightCapture);
                            validMoves.add(to);
                        }
                    }
                }
            }
        }
    }

    private void resetHighlights() {
        // ... (no changes here) ...
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? theme.lightSquare : theme.darkSquare);
            }
        }
    }

    private void highlightKingInCheck() {
        // ... (no changes here) ...
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board.getPieceAt(new Position(row, col));
                if (p instanceof King && p.isWhite == isWhiteTurn && board.isKingInCheck(isWhiteTurn)) {
                    squares[row][col].setBackground(theme.kingInCheck);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        // ... (no changes here) ...
        SwingUtilities.invokeLater(() -> {
            ChessGUI chess = new ChessGUI();
            chess.setVisible(true);
        });
    }
}