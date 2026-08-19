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
            String[] words = command.split(" ", 2);
            String commandWord = words[0];
            String arguments = words.length > 1 ? words[1] : "";

            if (commandWord.equals("unmark")) {
                int taskNumber = Integer.parseInt(arguments.trim());
                Task currTask = tasks.get(taskNumber - 1);
                currTask.markAsUndone();
                System.out.println(line);
                System.out.println("    The ravens retract their approval.");
                switch (currTask) {
                    case Todo todo ->
                            System.out.printf("     [T] [%s] %s\n", currTask.getStatusIcon(), currTask.getDescription());
                    case Deadline deadline ->
                            System.out.printf("     [D] [%s] %s (by: %s)\n", deadline.getStatusIcon(), deadline.getDescription(), deadline.getDeadline());
                    case Event event ->
                            System.out.printf("     [E] [%s] %s (from: %s to: %s)\n", event.getStatusIcon(), event.getDescription(), event.getStartTime(), event.getEndTime());
                    default -> {
                    }
                }
                System.out.println(line);
            }
            else if (commandWord.equals("mark")) {
                int taskNumber = Integer.parseInt(arguments.trim());
                Task currTask = tasks.get(taskNumber - 1);
                currTask.markAsDone();
                System.out.println(line);
                System.out.println("    One less burden to carry.");
                switch (currTask) {
                    case Todo todo ->
                            System.out.printf("     [T] [%s] %s\n", currTask.getStatusIcon(), currTask.getDescription());
                    case Deadline deadline ->
                            System.out.printf("     [D] [%s] %s (by: %s)\n", deadline.getStatusIcon(), deadline.getDescription(), deadline.getDeadline());
                    case Event event ->
                            System.out.printf("     [E] [%s] %s (from: %s to: %s)\n", event.getStatusIcon(), event.getDescription(), event.getStartTime(), event.getEndTime());
                    default -> {
                    }
                }
                System.out.println();
                System.out.println(line);
            }
            else if (commandWord.equals("todo")) {
                String description = arguments.trim();
                Todo todo = new Todo(description);
                tasks.add(todo);
                System.out.println("    You have accepted the following quest:");
                System.out.printf("        [T] [ ] %s", description);
                System.out.println();
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(line);
            }
            else if (commandWord.equals("deadline")) {
                String[] deadlineParts = arguments.split(" /by ", 2);
                String description = deadlineParts[0].trim();
                String by = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";
                Deadline deadline = new Deadline(description, by);
                tasks.add(deadline);
                System.out.println("    You have accepted the following quest:");
                System.out.printf("        [D] [ ] %s (by: %s)", description, by);
                System.out.println();
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(line);
            }
            else if (commandWord.equals("event")) {
                String[] fromParts = arguments.split(" /from ", 2);
                String description = fromParts[0].trim();
                String from = "";
                String to = "";
                if (fromParts.length > 1) {
                    String[] toParts = fromParts[1].split(" /to ", 2);
                    from = toParts[0].trim();
                    to = toParts.length > 1 ? toParts[1].trim() : "";
                }
                Event event = new Event(description, from, to);
                tasks.add(event);
                System.out.println("    You have accepted the following quest:");
                System.out.printf("        [E] [ ] %s (from: %s to: %s)", description, from, to);
                System.out.println();
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
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
                    if (currTask instanceof Todo) {
                        System.out.printf("     %d. [T] [%s] %s\n", i + 1, currTask.getStatusIcon(), currTask.getDescription());
                    }
                    else if (currTask instanceof Deadline) {
                        Deadline deadline = (Deadline) currTask;
                        System.out.printf("     %d. [D] [%s] %s (by: %s)\n", i + 1, deadline.getStatusIcon(), deadline.getDescription(), deadline.getDeadline());
                    }
                    else if (currTask instanceof Event) {
                        Event event = (Event) currTask;
                        System.out.printf("     %d. [E] [%s] %s (from: %s to: %s)\n", i + 1, event.getStatusIcon(), event.getDescription(), event.getStartTime(), event.getEndTime());
                    }
                }
                System.out.println(line);
            }
            else {
                System.out.println(line);
                System.out.println("     " + command);
                System.out.println(line);
                System.out.println();
            }
        }
    }
}
