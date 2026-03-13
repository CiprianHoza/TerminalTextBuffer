# TerminalBuffer

A robust Java implementation of a terminal buffer data structure designed for terminal emulation. This buffer manages a grid of cells with complex attributes, handles scrollback history, and implements editing logic such as cascading line wraps.

## Features

### Configuration
* Configurable initial width and height.
* Configurable maximum scrollback size (number of historical lines).

### Attributes and Styling
* 16 standard terminal colors for foreground and background.
* Support for "default" color states (-1).
* Bitmask-based font styles: Bold, Italic, and Underline.

### Cursor Management
* Get and set absolute cursor positions (X, Y).
* Relative movement: Move up, down, left, and right by N cells.
* Automatic clamping: The cursor is restricted within the screen boundaries to prevent out-of-bounds errors.

### Editing Operations
* Write: Overwrite existing content at the cursor position and advance the cursor.
* Insert: Add text at the cursor position, pushing existing content to the right with a cascading wrap effect (characters pushed off a line move to the start of the next line).
* Fill: Fill a specific line with a character using current attributes.
* Clear: Options to clear the visible screen, or clear both screen and scrollback history.

## Coordinate System

The buffer uses a relative indexing system to allow seamless access to both the visible screen and the history:

* y >= 0: Accesses the visible Screen (0 is the top-most visible line).
* y < 0: Accesses the Scrollback area (-1 is the most recent line that moved out of view).

The internal mapping formula is: `index = (totalLines - height) + y`.

## Technical Details

* Cell Storage: Uses 32-bit integers to store character codepoints, supporting Unicode and Emojis.
* Style Logic: Font styles are managed via bitwise operations for high performance.
* Memory Management: Automatically evicts the oldest lines once the scrollback limit is reached.

## Getting Started

### Prerequisites
* Java 17 or higher.
* Gradle 8.x.

### Build and Test
To compile the project and run the comprehensive JUnit 5 test suite:

```bash
# Run unit tests
./gradlew test

# Clean and run tests
./gradlew clean test
