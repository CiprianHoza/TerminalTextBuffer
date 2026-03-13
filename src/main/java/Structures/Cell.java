package Structures;

//the structure cell class for every character in the buffer
public class Cell {
    //I'll use bitmasking for setting up fonts
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;

    //the attribute 'content' will be type 'int' to support Unicode 32-bit characters
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
