package Buffer;

import Structures.*;
import java.util.*;

public class TerminalBuffer {
    //Terminal buffer attributes
    private final int width;
    private final int height;
    private final int maxxScrollBack;

    //All the terminal lines of cells will be grouped in an ArrayList
    private final ArrayList<Line> allLines;

    //default values for background and foreground
    private byte currentFg = -1;
    private byte currentBg = -1;

    //default font(bold, italic and underline are off)
    private int currentFont = 0;

    //Inner class Cursor with 2 attributes describing the X and Y axis
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

        //All the setters support negative input because using Math.max restricts the values
        //to be between 0 and height/width - 1
        //The screen area starts at level y = 0, everything that is lower than that is ScrollBack area
        //That is why I implemented this negative input support system
        public void setVertically(int lines)
        {
            cursorY = Math.max(0, Math.min(height - 1, cursorY + lines));
        }

        public void setHorizontally(int cells)
        {
            cursorX = Math.max(0, Math.min(width - 1, cursorX + cells));
        }

        public void setX(int x)
        {
            cursorX = Math.max(0, Math.min(width - 1, x));
        }

        public void setY(int y)
        {
            cursorY = Math.max(0, Math.min(height - 1, y));
        }
    }

    //Cursor is defined at starting position (0, 0)
    public Cursor cursor = new Cursor(0, 0);

    //Initialising the buffer
    public TerminalBuffer(int width, int height, int maxxScrollBack) {
        this.width = width;
        this.height = height;
        this.maxxScrollBack = maxxScrollBack;

        allLines = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            allLines.add(new Line(width));
        }
    }

    //#######################################################
    //Set Attributes

    //The font attributes will be set using bitwise operations on the 'currentFont' attribute
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
    //#######################################################

    //This method also supports negative input for the ScrollBack area
    //for example, for y = -7, the method will return the line in the ScrollBack area
    //relatively to the screen, so the 7th line before the starting line of the screen
    private Line getLine(int y)
    {
        int index = (allLines.size() - height) + y;
        if (index >= 0 && index < allLines.size())
            return allLines.get(index);
        return null;
    }

    //For every cursor position change, a series of verifications must be made to prevent
    //index out of bonds of the buffer
    private void validateMoveCursor()
    {
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

    //#######################################################
    //Editing operations

    //Write methods that replace the cell at current cursor's position
    public void write(int character)
    {
        Line currentLine = getLine(cursor.cursorY);
        Cell newCell = new Cell(character, currentFg, currentBg, currentFont);

        currentLine.write(cursor.cursorX, newCell);
        validateMoveCursor();
    }

    public void write(String text)
    {
        text.codePoints().forEach(this::write);
    }

    //The insert method will push to the right every cell after the cursor's position
    public void insert(String text)
    {
        text.codePoints().forEach(cp -> {
            Line currentLine = getLine(cursor.cursorY);
            Cell newCell = new Cell(cp, currentFg, currentBg, currentFont);
            int x = cursor.cursorX;
            int y = cursor.cursorY;

            //Insert will do content wrapping
            //Every cell that is pushed out of the line, will be inserted on the next one
            while (newCell != null && y < height)
            {
                Line line = getLine(y);

                if (line == null)
                    break;

                //Line.insert() returns the cell that is pushed out of the current line
                newCell = line.insert(x, newCell);
                x = 0;
                y++;
            }
            validateMoveCursor();
        });
    }

    //This method will push up every line, adding at the bottom an empty line
    public void scrollUp()
    {
        allLines.add(new Line(this.width));

        //If the ScrollBack area becomes to large, the oldest line will be removed
        if (allLines.size() > height + maxxScrollBack) {
            allLines.remove(0);
        }
    }

    //Filling an entire line with a specific character with current attributes
    public void fillLine(int index, int character)
    {
        Line currentLine = getLine(index);

        for (int i = 0; i < width; i++)
            currentLine.write(i, new Cell(character, currentFg, currentBg, currentFont));
    }
    //#######################################################

    //#######################################################
    //Clear Operations

    //clearAll() will reset the buffer
    public void clearAll()
    {
        allLines.clear();
        this.cursor = new Cursor(0, 0);

        for (int i = 0; i < height; i++)
            allLines.add(new Line(width));
    }

    //The screen area content will be deleted
    public void clearScreen()
    {
        //Setting the cursor position at the starting point (0, 0)
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
        return -1;
    }

    public int getForeGround(int x, int y)
    {
        Line currentLine = getLine(y);
        if (currentLine != null)
            return currentLine.getCell(x).foreground;
        return -1;
    }

    public String getLineString(int y)
    {
        return getLine(y).toString();
    }

    //This method will return the content of the entire screen area
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

    //This method will return the entire content of the buffer
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
