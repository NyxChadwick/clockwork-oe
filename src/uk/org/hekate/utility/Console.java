package uk.org.hekate.utility;

import java.text.MessageFormat;
import org.jetbrains.annotations.*;


public final class Console {
    @NotNull private static final String _lineSeparator = System.getProperty("line.separator");
    @NotNull private static final ConsoleState _state = new ConsoleState();


    public enum Colour
    {
        Black(0), Red(1), Green(2), Yellow(3), Blue(4), Magenta(5), Cyan(6), White(7);

        private final int _index;

        Colour(int index) { _index = index; }

        public int index() { return _index; }
    }


    public final static class ConsoleState {
        private Boolean _isAnsiColour = false;
        private Colour _background = null;
        private Colour _foreground = null;


        public void setAnsiColourAutomatic() { setAnsiColour(System.getProperty("os.name").startsWith("Windows")); }
        public void setAnsiColour(boolean isEnabled) { _isAnsiColour = isEnabled; }
        public void setBackground(Colour colour) { _background = colour; }
        public void setForeground(Colour colour) { _foreground = colour; }


        public void writeLine() { write(_lineSeparator); }
        public void writeLine(@NotNull String format, Object... arguments) { write(format+_lineSeparator, arguments); }


        public void write(@NotNull String format, Object... arguments) {
            String text = (arguments == null || arguments.length == 0)? format:
                    MessageFormat.format(format, arguments);

            synchronized (_state) {
                String prefix = null;
                String suffix;

                if (_isAnsiColour && (_foreground != null || _background != null))
                {
                    String colourFormat =
                            (_foreground == null)? "\u001B[[4{0}m{2}\u001B[0m":
                                    (_background == null)? "\u001B[3{1}m{2}\u001B[0m":
                                            "\u001B[4{0};[3{1}m{2}\u001B[0m";

                    text = MessageFormat.format(colourFormat,
                            _background == null? null: _background.index(),
                            _foreground == null? null: _foreground.index(),
                            text);
                }
                System.out.print(text);
            }
        }
    }


    public static void writeLine() { _state.writeLine(); }
    public static void writeLine(@NotNull String format, Object... arguments) { _state.writeLine(format, arguments); }
    public static void write(@NotNull String format, Object... arguments) { _state.write(format, arguments); }
}
