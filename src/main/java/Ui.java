import java.util.Scanner;

/**
 * Interactions with the user through the console.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private static final String BANNER = " __          __       _\n"
            + " \\ \\        / /      | |\n"
            + "  \\ \\  /\\  / /__   __| | __ _ _ __\n"
            + "   \\ \\/  \\/ / _ \\ / _` |/ _` | '_ \\\n"
            + "    \\  /\\  / (_) | (_| | (_| | | | |\n"
            + "     \\/  \\/ \\___/ \\__,_|\\__,_|_| |_|\n";

    private final Scanner in;

    /**
     * Creates a UI that reads from standard input and writes to standard output.
     */
    public Ui() {
        this.in = new Scanner(System.in);
    }

    /**
     * Shows the banner and greeting.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println(LINE);
        System.out.println("     Hail, wanderer. Wodan is listening.");
        System.out.println("     What is your command?");
        System.out.println(LINE);
        System.out.println();
    }

    /**
     * Returns whether another line of user input is available.
     */
    public boolean hasCommand() {
        return in.hasNextLine();
    }

    /**
     * Returns the next line typed by the user.
     */
    public String readCommand() {
        return in.nextLine();
    }

    /**
     * Shows {@code message} between the usual reply lines.
     *
     * @param message Text to show the user.
     */
    public void showError(String message) {
        System.out.println(LINE);
        System.out.println("     " + message);
        System.out.println(LINE);
    }

    /**
     * Shows the farewell message.
     */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("     So it is written. Farewell, wanderer.");
        System.out.println(LINE);
    }

    /**
     * Shows the horizontal rule used around chatbot replies.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Shows one line of text, followed by a newline.
     *
     * @param text Line to print, which may be empty.
     */
    public void show(String text) {
        System.out.println(text);
    }

    /**
     * Shows a numbered task in a list, using 1-based {@code index}.
     *
     * @param index One-based position in the list.
     * @param task Task to display.
     */
    public void showNumberedTask(int index, Task task) {
        System.out.printf("     %d. %s\n", index, task.toString());
    }
}
