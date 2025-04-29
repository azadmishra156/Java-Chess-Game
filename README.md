# ♟️ Java Chess Game

A complete 2-player chess game developed in Java using Swing for the graphical interface. This project features theming, move highlighting, check/checkmate detection, save/load functionality, and a polished, user-friendly GUI.

---

## 🎯 Features

- ✔️ Full implementation of standard chess rules
- ✔️ GUI-based board using Java Swing
- ✔️ Click-based piece movement
- ✔️ Highlighting for:
  - Selected pieces
  - Valid moves
  - Capturable enemy pieces
  - King in check
- ✔️ Check and checkmate detection
- ✔️ Pawn promotion with player selection
- ✔️ Undo functionality (coming soon)
- ✔️ Save and load game state
- ✔️ Restart and exit options
- ✔️ Theme switching: Classic, Dark, and Ice
- ✔️ Visual feedback and hover effects

---

## 📁 Project Structure
Java-Chess-Game/
│
├── Chess/
│   ├── Board.java           # Handles game board setup and move logic
│   ├── ChessGUI.java        # Main graphical user interface
│   ├── Piece.java           # Abstract base class for all pieces
│   ├── Pawn.java            # Pawn-specific logic
│   ├── Rook.java            # Rook-specific logic
│   ├── Knight.java          # Knight-specific logic
│   ├── Bishop.java          # Bishop-specific logic
│   ├── Queen.java           # Queen-specific logic
│   ├── King.java            # King-specific logic
│   ├── Position.java        # Represents a square on the board
│   └── Theme.java           # Enum for UI themes
│
├── assets/
│   └── icons/               # Folder for piece images (e.g. white_pawn.png)
│       ├── classic/
│       ├── dark/
│       └── ice/
│
├── saved_game.ser           # Serialized file for saving game state (created at runtime)
├── README.md                # Project overview and setup instructions


## ⚙️ How to Set Up and Run the Project

### ✅ Prerequisites

- Java JDK 17 or higher
- A Java IDE (e.g., IntelliJ IDEA, Eclipse) OR command line terminal
- Image resources (PNG files) for chess pieces in `/resources/icons/theme-name/` directories

---

### ▶️ Run via Command Line

1. **Clone the Repository**

   ```bash
   git clone https://github.com/azadmishra156/Java-c\Chess-Game.git
   cd java-chess-game
   
2. **Compile the Source Code**
   javac -d bin src/Chess/*.java
   
3. **Run the Game**
   java -cp bin Chess.ChessGUI   

