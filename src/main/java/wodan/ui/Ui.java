package wodan.ui;

import java.io.PrintStream;
import java.util.Scanner;

import wodan.task.Task;

/**
 * Interactions with the user through the console, or through a captured stream for the GUI.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private static final String BANNER = " __          __       _\n"
            + " \\ \\        / /      | |\n"
            + "  \\ \\  /\\  / /__   __| | __ _ _ __\n"
            + "   \\ \\/  \\/ / _ \\ / _` |/ _` | '_ \\\n"
            + "    \\  /\\  / (_) | (_| | (_| | | | |\n"
            + "     \\/  \\/ \\___/ \\__,_|\\__,_|_| |_|\n";

    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Creates a UI that reads from standard input and writes to standard output.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.out = System.out;
    }

    /**
     * Creates a UI that writes replies to {@code out} and does not read commands.
     *
     * @param out Destination for chatbot replies.
     */
    public Ui(PrintStream out) {
        this.scanner = null;
        this.out = out;
    }

    /**
     * Shows the banner and greeting.
     */
    public void showWelcome() {
        out.println(BANNER);
        show(LINE,
                "     Hail, wanderer. Wodan is listening.",
                "     What is your command?",
                LINE,
                "");
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return {@code true} if a command line can be read.
     */
    public boolean hasCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line typed by the user.
     *
     * @return The raw command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows an error message. The surrounding divider lines are drawn by {@code Wodan.run}.
     *
     * @param message Text to show the user.
     */
    public void showError(String message) {
        out.println("     " + message);
    }

    /**
     * Shows the farewell message. The surrounding divider lines are drawn by {@code Wodan.run}.
     */
    public void showGoodbye() {
        out.println("     So it is written. Farewell, wanderer.");
    }

    /**
     * Shows the horizontal rule used around chatbot replies.
     */
    public void showLine() {
        out.println(LINE);
    }

    /**
     * Shows each of {@code lines} on its own line.
     *
     * @param lines Lines to print, which may be empty strings. Zero lines prints nothing.
     */
    public void show(String... lines) {
        for (String line : lines) {
            out.println(line);
        }
    }

    /**
     * Shows a numbered task in a list, using 1-based {@code index}.
     *
     * @param index One-based position in the list.
     * @param task Task to display.
     */
    public void showNumberedTask(int index, Task task) {
        out.printf("     %d. %s\n", index, task.toString());
    }
}
