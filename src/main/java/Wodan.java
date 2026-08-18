import java.util.Scanner;

/**
 * Entry point for the Wodan chatbot.
 * Prints a few ASCII-art banner variations for the mixed-case name "Wodan".
 */
public class Wodan {
    public static void main(String[] args) {
        String banner = " __          __       _\n"
                + " \\ \\        / /      | |\n"
                + "  \\ \\  /\\  / /__   __| | __ _ _ __\n"
                + "   \\ \\/  \\/ / _ \\ / _` |/ _` | '_ \\\n"
                + "    \\  /\\  / (_) | (_| | (_| | | | |\n"
                + "     \\/  \\/ \\___/ \\__,_|\\__,_|_| |_|\n";

        String line = "    ____________________________________________________________";

        System.out.println(banner);
        System.out.println(line);
        System.out.println("     Hello! I'm Wodan.");
        System.out.println("     What can I do for you?");
        System.out.println(line);
        System.out.println();
        Scanner in = new Scanner(System.in);
        while (true) {
            String command = in.nextLine();
            if (command.equals("bye")) {
                System.out.println(line);
                System.out.println("     Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }
            System.out.println(line);
            System.out.println("     " + command);
            System.out.println(line);
            System.out.println();
        }
    }
}
