package Structures;
import java.util.*;

public class Line {
    private Cell[] cells;

    public Line(int width)
    {
        this.cells = new Cell[width];
        for (int i = 0; i < width; i++)
            cells[i] = new Cell(' ', (byte)0, (byte)0, 0);
    }

    public void insert(int x, Cell newCell)
    {
        if (x < 0 || x >= cells.length) return;

        for (int i = this.cells.length - 1; i > x; i--)
            this.cells[i] = this.cells[i - 1];
        this.cells[x] = newCell;
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
        StringBuilder sb = new StringBuilder();
        for (Cell ch : this.cells)
            sb.appendCodePoint(ch.content);
        return sb.toString();
    }
}
