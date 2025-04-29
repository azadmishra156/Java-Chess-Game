package Chess;

import java.awt.Color;
import java.io.Serializable;

public class Theme implements Serializable {
    public final Color lightSquare;
    public final Color darkSquare;
    public final Color selectedSquare;
    public final Color highlightMove;
    public final Color highlightCapture;
    public final Color kingInCheck;
    public final String iconFolder;

    public Theme(Color light, Color dark, Color selected, Color move, Color capture, Color check, String folder) {
        this.lightSquare = light;
        this.darkSquare = dark;
        this.selectedSquare = selected;
        this.highlightMove = move;
        this.highlightCapture = capture;
        this.kingInCheck = check;
        this.iconFolder = folder;
    }

    public static final Theme CLASSIC = new Theme(
        new Color(240, 217, 181),  // light
        new Color(181, 136, 99),   // dark
        Color.YELLOW,              // selected
        new Color(100, 255, 100),  // move
        new Color(255, 100, 100),  // capture
        new Color(255, 0, 0),      // king in check
        "/resources/classic/"      // folder
    );

    public static final Theme DARK = new Theme(
        new Color(50, 50, 50),
        new Color(30, 30, 30),
        Color.ORANGE,
        new Color(100, 150, 100),
        new Color(200, 100, 100),
        Color.RED,
        "/resources/dark/"
    );

    public static final Theme ICE = new Theme(
        new Color(220, 240, 255),
        new Color(140, 180, 200),
        new Color(100, 170, 255),
        new Color(170, 255, 255),
        new Color(255, 170, 170),
        new Color(255, 100, 100),
        "/resources/ice/"
    );
}
