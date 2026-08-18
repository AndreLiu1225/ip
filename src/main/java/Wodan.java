import java.util.ArrayList;
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

        // Array to store tasks
        ArrayList<String> tasks = new ArrayList<String>();
        int idx = 0;

        System.out.println(banner);
        System.out.println(line);
        System.out.println("     Hail, wanderer. Wodan is listening.");
        System.out.println("     What is your command?");
        System.out.println(line);
        System.out.println();
        Scanner in = new Scanner(System.in);
        while (true) {
            String command = in.nextLine();
            if (command.equals("bye")) {
                System.out.println(line);
                System.out.println("     So it is written. Farewell, wanderer.");
                System.out.println(line);
                break;
            }
            if (command.equals("list")) {
                System.out.println(line);
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.printf("     %d. %s%n", i + 1, tasks.get(i));
                }
                System.out.println(line);
                continue;
            }
            System.out.println(line);
            System.out.println("     " + command);
            System.out.println(line);
            System.out.println();

            tasks.add(command);
        }
    }
}
