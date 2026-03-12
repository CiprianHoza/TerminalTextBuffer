import Structures.*;
import java.util.*;

public class TerminalBuffer {
    private final int width;
    private final int height;
    private final int maxxScrollBack;

    private final ArrayList<Line> allLines;

    //default values for background and foreground
    private byte currentFg = -1;
    private byte currentBg = -1;

    //default font(bold, italic and underline are off)
    private int currentFont = 0;

    public class Cursor {
        private int cursorX, cursorY;

        public Cursor(int cursorX, int cursorY)
        {
            this.cursorX = cursorX;
            this.cursorY = cursorY;
        }

        public int getPositionByX()
        {
            return cursorX;
        }

        public int getPositionByY()
        {
            return cursorY;
        }

        public void setVertically(int lines)
        {
            cursorY = Math.max(0, Math.min(height - 1, cursorY + lines));
        }

        public void setHorizontally(int cells)
        {
            cursorX = Math.max(0, Math.min(width - 1, cursorX + cells));
        }
    }

    public Cursor cursor = new Cursor(0, 0);

    public TerminalBuffer(int width, int height, int maxxScrollBack) {
        this.width = width;
        this.height = height;
        this.maxxScrollBack = maxxScrollBack;

        allLines = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            allLines.add(new Line(width));
        }
    }

    public void setBold(boolean isBold)
    {
        if (isBold)
            currentFont |= Cell.BOLD;
        else
            currentFont &= ~Cell.BOLD;
    }

    public void setItalic(boolean italic)
    {
        if (italic)
            currentFont |= Cell.ITALIC;
        else
            currentFont &= ~Cell.ITALIC;
    }

    public void setUnderline(boolean underline)
    {
        if (underline)
            currentFont |= Cell.UNDERLINE;
        else
            currentFont &= ~Cell.UNDERLINE;
    }

    public void setAttributes(byte fg, byte bg, int ft)
    {
        currentFg = fg;
        currentBg = bg;
        currentFont = ft;
    }

    private Line getLine(int y)
    {
        int index = (allLines.size() - height) + y;
        return allLines.get(index);
    }

    public void write(int character)
    {
        Line currentLine = getLine(cursor.cursorY);
        Cell newCell = new Cell(character, currentFg, currentBg, currentFont);

        currentLine.write(cursor.cursorX, newCell);
        cursor.cursorX++;
        if (cursor.cursorX >= this.width) {
            cursor.cursorX = 0;
            cursor.cursorY++;

            if (cursor.cursorY >= this.height) {
                cursor.cursorY = height - 1;
                scrollUp();
            }
        }
    }

    public void scrollUp()
    {
        allLines.add(new Line(this.width));

        if (allLines.size() > height + maxxScrollBack) {
            allLines.remove(0);
        }
    }

    public void fillLine(int index, int character)
    {
        Line currentLine = getLine(index);

        for (int i = 0; i < width; i++)
            currentLine.write(i, new Cell(character, currentFg, currentBg, currentFont));
    }

    //#######################################################
    //Clear Operations
    public void clearAll()
    {
        allLines.clear();
        this.cursor = new Cursor(0, 0);

        for (int i = 0; i < height; i++)
            allLines.add(new Line(width));
    }

    public void clearScreen()
    {
        cursor.setVertically(-height);
        cursor.setHorizontally(-width);

        for (int i = 0; i < height; i++)
            fillLine(i, ' ');
    }
    //#######################################################

    //#######################################################
    //Content Acces

    public int getCharacter(int x, int y)
    {
        Line currentLine = getLine(y);
        if (currentLine != null)
            return currentLine.getCell(x).content;
        return -1;
    }

    public int getFont(int x, int y)
    {
        Line currentLine = getLine(y);
        if (currentLine != null)
            return currentLine.getCell(x).font;
        return -1;
    }

    public int getBackGround(int x, int y)
    {
        Line currentLine = getLine(y);
        if (currentLine != null)
            return currentLine.getCell(x).background;
        return -2;
    }

    public int getForeGround(int x, int y)
    {
        Line currentLine = getLine(y);
        if (currentLine != null)
            return currentLine.getCell(x).foreground;
        return -2;
    }

    public String getLineString(int y)
    {
        return getLine(y).toString();
    }

    public String getScreenString()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < height; i++) {
            Line currentLine = getLine(i);
            if (currentLine != null)
            {
                sb.append(currentLine);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String getScrAllString()
    {
        StringBuilder sb = new StringBuilder();
        for (Line currentLine : allLines)
        {
            sb.append(currentLine);
            sb.append("\n");
        }
        return sb.toString();
    }
    //#######################################################

}
