package Structures;

import Buffer.TerminalBuffer;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TerminalBuffer Tests")
public class TerminalBufferTest {

    private TerminalBuffer buffer;
    private final int width = 80;
    private final int height = 24;
    private final int maxScrollBack = 100;

    @BeforeEach
    public void setup()
    {
        buffer = new TerminalBuffer(width, height, maxScrollBack);
    }

    @Test
    @DisplayName("Should correctly combine and toggle multiple font style using bitmasking")
    void testBitmasking()
    {
        //Toggle the bold and italic fonts
        buffer.setBold(true);
        buffer.setItalic(true);
        buffer.write('a');

        int fontA = buffer.getFont(0, 0);
        assertTrue((fontA & Cell.BOLD) != 0, "Bold bit should be set");
        assertTrue((fontA & Cell.ITALIC) != 0, "Italic bit should be set");
        assertFalse((fontA & Cell.UNDERLINE) != 0, "Underline bit should not be set");

        //Deactivate the bold bit and toggle the underline font
        buffer.setBold(false);
        buffer.setUnderline(true);
        buffer.write('b');

        int fontB = buffer.getFont(1, 0);
        assertTrue((fontB & Cell.UNDERLINE) != 0, "Underline bit should be set");
        assertTrue((fontB & Cell.ITALIC) != 0, "Italic bit should be set");
        assertFalse((fontB & Cell.BOLD) != 0, "Bold bit should not be set");
    }

    @Test
    @DisplayName("Insert at start or full screen should cascade characters down multiple lines")
    void testInsertCascade()
    {
        //Filling the first 3 lines with A, B, C
        buffer.fillLine(0, 'A');
        buffer.fillLine(1, 'B');
        buffer.fillLine(2, 'C');

        //Resetting the cursor and inserting the character 'X' at the start
        buffer.cursor.setX(0);
        buffer.cursor.setY(0);
        buffer.insert("X");

        //The first character should be 'X' and the second 'A'
        assertEquals('X', (char)buffer.getCharacter(0, 0));
        assertEquals('A', (char)buffer.getCharacter(1, 0));

        //On the second line, the first character should be 'A' and the rest 'B'
        assertEquals('A', (char)buffer.getCharacter(1, 0));
        assertEquals('B', (char)buffer.getCharacter(1, 1));

        //On the third line, the first character should be 'B' and the rest 'C'
        assertEquals('B', (char)buffer.getCharacter(0, 2));
        assertEquals('C', (char)buffer.getCharacter(1, 2));

        //On the forth line, the only character should be 'C'
        assertEquals('C', (char)buffer.getCharacter(0, 3));
        assertEquals(' ', (char)buffer.getCharacter(1, 3));
    }

    @Test
    @DisplayName("Should perfectly maintain scrollback integrity during data influx")
    void testDataInflux()
    {
        //Writing on the first 500 lines
        for (int i = 0; i < 500; i++)
        {
            buffer.write("Line " + String.format("%03d", i));
            buffer.scrollUp();
        }
        //because of the height = 24 and maxxScrollBack = 100, the maximum length of screen + scrollback
        //should be 124 lines

        String data = buffer.getScrAllString();
        assertFalse(data.contains("Line 000"), "The oldest line should be gone");
        assertFalse(data.contains("Line 375"), "The line 375 should be evicted");
        assertTrue(data.contains("Line 400"), "Line 400 should be the new start of the scrollback");
    }

}
