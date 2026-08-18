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
        ArrayList<Task> tasks = new ArrayList<Task>();

        System.out.println(banner);
        System.out.println(line);
        System.out.println("     Hail, wanderer. Wodan is listening.");
        System.out.println("     What is your command?");
        System.out.println(line);
        System.out.println();
        Scanner in = new Scanner(System.in);

        while (true) {
            String command = in.nextLine();
            String[] parts = command.split(" ");

            if (parts[0].equals("mark")) {
                int taskNumber = Integer.parseInt(parts[1]);
                Task currTask = tasks.get(taskNumber - 1);
                currTask.markAsDone();
                System.out.println(line);
                System.out.println("    One less burden to carry.");
                System.out.printf("        [%s] %s", currTask.getStatusIcon(), currTask.getDescription());
                System.out.println();
                System.out.println(line);
            }
            else if (parts[0].equals("unmark")) {
                int taskNumber = Integer.parseInt(parts[1]);
                Task currTask = tasks.get(taskNumber - 1);
                currTask.markAsUndone();
                System.out.println(line);
                System.out.println("    The ravens retract their approval.");
                System.out.printf("        [%s] %s", currTask.getStatusIcon(), currTask.getDescription());
                System.out.println();
                System.out.println(line);
            }
            else if (command.equals("bye")) {
                System.out.println(line);
                System.out.println("     So it is written. Farewell, wanderer.");
                System.out.println(line);
                break;
            }
            else if (command.equals("list")) {
                System.out.println(line);
                System.out.println("    The ravens have given these quests.\n");
                for (int i = 0; i < tasks.size(); i++) {
                    Task currTask = tasks.get(i);
                    System.out.printf("     %d. [%s] %s\n", i + 1, currTask.getStatusIcon(), currTask.getDescription());
                }
                System.out.println(line);
            }
            else {
                System.out.println(line);
                System.out.println("     " + command);
                System.out.println(line);
                System.out.println();

                tasks.add(
                        new Task(command)
                );
            }
        }
    }
}
