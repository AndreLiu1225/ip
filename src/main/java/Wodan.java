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

        label:
        while (true) {
            String command = in.nextLine();
            try {
                if (command.trim().isEmpty()) {
                    throw new WodanException(
                            "Silence is not a command. Speak todo, deadline, event, list, mark, unmark, delete, or bye.");
                }

                String[] words = command.trim().split(" ", 2);
                String commandWord = words[0];
                String arguments = words.length > 1 ? words[1] : "";

                switch (commandWord) {
                    case "delete": {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest is too burdensome? Try: delete 1");
                        }
                        int taskNumber;
                        try {
                            taskNumber = Integer.parseInt(arguments.trim());
                        } catch (NumberFormatException e) {
                            throw new WodanException(
                                    "'" + arguments.trim() + "' is not a quest number. Try: delete 1");
                        }
                        if (tasks.isEmpty()) {
                            throw new WodanException(
                                    "There are no quests to delete yet. Add one with todo, deadline, or event.");
                        }
                        if (taskNumber < 1 || taskNumber > tasks.size()) {
                            throw new WodanException(
                                    "There is no quest " + taskNumber + ". The ravens watch over " + tasks.size()
                                            + (tasks.size() == 1 ? " quest" : " quests")
                                            + ". Try a number from 1 to " + tasks.size() + ".");
                        }
                        Task tbr = tasks.remove(taskNumber - 1);
                        System.out.println(line);
                        System.out.println("     Noted. I've removed this task:");
                        System.out.println("       " + tbr.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case "unmark": {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest should the ravens unmark? Try: unmark 1");
                        }
                        int taskNumber;
                        try {
                            taskNumber = Integer.parseInt(arguments.trim());
                        } catch (NumberFormatException e) {
                            throw new WodanException(
                                    "'" + arguments.trim() + "' is not a quest number. Try: unmark 1");
                        }
                        if (tasks.isEmpty()) {
                            throw new WodanException(
                                    "There are no quests to unmark yet. Add one with todo, deadline, or event.");
                        }
                        if (taskNumber < 1 || taskNumber > tasks.size()) {
                            throw new WodanException(
                                    "There is no quest " + taskNumber + ". The ravens watch over " + tasks.size()
                                            + (tasks.size() == 1 ? " quest" : " quests")
                                            + ". Try a number from 1 to " + tasks.size() + ".");
                        }
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsUndone();
                        System.out.println(line);
                        System.out.println("    The ravens retract their approval.");
                        System.out.println("     " + currTask.toString());
                        System.out.println(line);
                        break;
                    }
                    case "mark": {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest should the ravens mark? Try: mark 1");
                        }
                        int taskNumber;
                        try {
                            taskNumber = Integer.parseInt(arguments.trim());
                        } catch (NumberFormatException e) {
                            throw new WodanException(
                                    "'" + arguments.trim() + "' is not a quest number. Try: mark 1");
                        }
                        if (tasks.isEmpty()) {
                            throw new WodanException(
                                    "There are no quests to mark yet. Add one with todo, deadline, or event.");
                        }
                        if (taskNumber < 1 || taskNumber > tasks.size()) {
                            throw new WodanException(
                                    "There is no quest " + taskNumber + ". The ravens watch over " + tasks.size()
                                            + (tasks.size() == 1 ? " quest" : " quests")
                                            + ". Try a number from 1 to " + tasks.size() + ".");
                        }
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsDone();
                        System.out.println(line);
                        System.out.println("    One less burden to carry.");
                        System.out.println("     " + currTask.toString());
                        System.out.println();
                        System.out.println(line);
                        break;
                    }
                    case "todo": {
                        String description = arguments.trim();
                        if (description.isEmpty()) {
                            throw new WodanException(
                                    "A todo needs a quest name. Try: todo borrow book");
                        }
                        Todo todo = new Todo(description);
                        tasks.add(todo);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + todo.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case "deadline": {
                        String[] deadlineParts = arguments.split(" /by ", 2);
                        String description = deadlineParts[0].trim();
                        String by = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";
                        if (description.startsWith("/by")) {
                            throw new WodanException(
                                    "A deadline needs a quest name before /by. Try: deadline return book /by Sunday");
                        }
                        if (description.isEmpty()) {
                            throw new WodanException(
                                    "A deadline needs a quest name and /by <when>. Try: deadline return book /by Sunday");
                        }
                        if (deadlineParts.length < 2) {
                            throw new WodanException(
                                    "A deadline must include /by <when>. Try: deadline return book /by Sunday");
                        }
                        if (by.isEmpty()) {
                            throw new WodanException(
                                    "The ravens need a time after /by. Try: deadline return book /by Sunday");
                        }
                        Deadline deadline = new Deadline(description, by);
                        tasks.add(deadline);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + deadline.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case "event": {
                        String[] fromParts = arguments.split(" /from ", 2);
                        String description = fromParts[0].trim();
                        if (description.startsWith("/from")) {
                            throw new WodanException(
                                    "An event needs a quest name before /from. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        if (description.isEmpty()) {
                            throw new WodanException(
                                    "An event needs a name, /from <start>, and /to <end>. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        if (fromParts.length < 2) {
                            throw new WodanException(
                                    "An event must include /from <start> and /to <end>. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        String[] toParts = fromParts[1].split(" /to ", 2);
                        String from = toParts[0].trim();
                        String to = toParts.length > 1 ? toParts[1].trim() : "";
                        if (from.isEmpty()) {
                            throw new WodanException(
                                    "The ravens need a start time after /from. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        if (toParts.length < 2) {
                            throw new WodanException(
                                    "An event must include /to <end>. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        if (to.isEmpty()) {
                            throw new WodanException(
                                    "The ravens need an end time after /to. Try: event meeting /from Mon 2pm /to 4pm");
                        }
                        Event event = new Event(description, from, to);
                        tasks.add(event);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + event.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case "bye":
                        System.out.println(line);
                        System.out.println("     So it is written. Farewell, wanderer.");
                        System.out.println(line);
                        break label;
                    case "list":
                        System.out.println(line);
                        System.out.println("    The ravens have given these quests.\n");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.printf("     %d. %s\n", i + 1, tasks.get(i).toString());
                        }
                        System.out.println(line);
                        break;
                    default:
                        throw new WodanException(
                                "That rune is unknown. Speak todo, deadline, event, list, mark, unmark, delete, or bye.");
                }
            } catch (WodanException e) {
                System.out.println(line);
                System.out.println("     " + e.getMessage());
                System.out.println(line);
            }
        }
    }
}
