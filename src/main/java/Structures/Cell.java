package Structures;

public class Cell {
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;

    public int content;
    public byte foreground;
    public byte background;
    public int font;

    public Cell(int character, byte foreground, byte background, int font) {
        this.content = character;
        this.foreground = foreground;
        this.background = background;
        this.font = font;
    }
}
