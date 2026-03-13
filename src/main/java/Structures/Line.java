package Structures;
import java.util.*;

//For better understanding every line of cells will be grouped in a 'line' structure
public class Line {
    private Cell[] cells;

    public Line(int width)
    {
        //At first, the cells will have the default configuration of attributes ant content
        this.cells = new Cell[width];
        for (int i = 0; i < width; i++)
            cells[i] = new Cell(' ', (byte)-1, (byte)-1, 0);
    }

    //This will insert a cell at a certain position on the line, moving every other cell to the right
    public Cell insert(int x, Cell newCell)
    {
        if (x < 0 || x >= cells.length) return null;

        //the method returns the last cell on the line before the insertion because of content wrapping
        Cell lastCell = cells[cells.length - 1];

        for (int i = this.cells.length - 1; i > x; i--)
            this.cells[i] = this.cells[i - 1];
        this.cells[x] = newCell;

        return lastCell;
    }

    public void write(int x, Cell newCell)
    {
        if (x >= 0 && x < cells.length)
            this.cells[x] = newCell;
    }

    public Cell getCell(int x)
    {
        return cells[x];
    }

    @Override
    public String toString()
    {
        //I'll use stringbuilder to build strings because String is an immutable structure
        StringBuilder sb = new StringBuilder();
        for (Cell ch : this.cells)
            //I use appendCodePoint() instead of just append() because 'content' is type 'int'
            sb.appendCodePoint(ch.content);
        return sb.toString();
    }
}
