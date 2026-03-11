import Structures.*;
import java.util.*;

public class TerminalBuffer {
    private final int width;
    private final int height;
    private final int maxxScrollBack;

    private final ArrayList<Line> allLines;
    private int cursorX = 0;
    private int cursorY = 0;
    private byte currentFg = 0;
    private byte currentBg = 0;
    private int currentFont = 0;

    public TerminalBuffer(int width, int height, int maxxScrollBack) {
        this.width = width;
        this.height = height;
        this.maxxScrollBack = maxxScrollBack;

        allLines = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            allLines.add(new Line(width));
        }
    }

    private Line getLine(int y)
    {
        int index = (allLines.size() - height) + y;
        return allLines.get(index);
    }
}
