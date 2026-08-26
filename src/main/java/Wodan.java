import java.util.ArrayList;
import java.util.Scanner;

/**
 * Entry point for the Wodan chatbot.
 * Prints a few ASCII-art banner variations for the mixed-case name "Wodan".
 * Saves the task list to {@code data/wodan.txt} (relative to the working directory)
 * whenever it changes.
 * Loads the task list from that file when the chatbot starts.
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

        Storage storage = new Storage();
        ArrayList<Task> tasks = new ArrayList<Task>();
        String startMessage = null;
        try {
            tasks = storage.load();
            startMessage = storage.getLoadWarning();
        } catch (WodanException e) {
            startMessage = e.getMessage();
        }

        System.out.println(banner);
        System.out.println(line);
        System.out.println("     Hail, wanderer. Wodan is listening.");
        System.out.println("     What is your command?");
        System.out.println(line);
        System.out.println();
        if (startMessage != null) {
            printMessage(line, startMessage);
        }
        Scanner in = new Scanner(System.in);

        label:
        while (true) {
            if (!in.hasNextLine()) {
                break label;
            }
            String command = in.nextLine();
            try {
                if (command.trim().isEmpty()) {
                    throw new WodanException(
                            "Silence is not a command. Speak todo, deadline, event, list, mark, unmark, delete, or bye.");
                }

                String[] words = command.trim().split(" ", 2);
                String commandWord = words[0];
                String arguments = words.length > 1 ? words[1] : "";

                switch (Command.parse(commandWord)) {
                    case DELETE: {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest is too burdensome? Try: delete 1");
                        }
                        int taskNumber = getTaskNumber(arguments, "' is not a quest number. Try: delete 1", tasks, "There are no quests to delete yet. Add one with todo, deadline, or event.");
                        Task tbr = tasks.remove(taskNumber - 1);
                        storage.save(tasks);
                        System.out.println(line);
                        System.out.println("     Noted. I've removed this task:");
                        System.out.println("       " + tbr.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case UNMARK: {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest should the ravens unmark? Try: unmark 1");
                        }
                        int taskNumber = getTaskNumber(arguments, "' is not a quest number. Try: unmark 1", tasks, "There are no quests to unmark yet. Add one with todo, deadline, or event.");
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsUndone();
                        storage.save(tasks);
                        System.out.println(line);
                        System.out.println("    The ravens retract their approval.");
                        System.out.println("     " + currTask.toString());
                        System.out.println(line);
                        break;
                    }
                    case MARK: {
                        if (arguments.trim().isEmpty()) {
                            throw new WodanException(
                                    "Which quest should the ravens mark? Try: mark 1");
                        }
                        int taskNumber = getTaskNumber(arguments, "' is not a quest number. Try: mark 1", tasks, "There are no quests to mark yet. Add one with todo, deadline, or event.");
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsDone();
                        storage.save(tasks);
                        System.out.println(line);
                        System.out.println("    One less burden to carry.");
                        System.out.println("     " + currTask.toString());
                        System.out.println();
                        System.out.println(line);
                        break;
                    }
                    case TODO: {
                        String description = arguments.trim();
                        if (description.isEmpty()) {
                            throw new WodanException(
                                    "A todo needs a quest name. Try: todo borrow book");
                        }
                        rejectFileDelimiter(description);
                        Todo todo = new Todo(description);
                        tasks.add(todo);
                        storage.save(tasks);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + todo.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case DEADLINE: {
                        Deadline deadline = getDeadline(arguments);
                        tasks.add(deadline);
                        storage.save(tasks);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + deadline.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case EVENT: {
                        Event event = getEvent(arguments);
                        tasks.add(event);
                        storage.save(tasks);
                        System.out.println("    You have accepted the following quest:");
                        System.out.println("        " + event.toString());
                        System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(line);
                        break;
                    }
                    case BYE:
                        System.out.println(line);
                        System.out.println("     So it is written. Farewell, wanderer.");
                        System.out.println(line);
                        break label;
                    case LIST:
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
                printMessage(line, e.getMessage());
            }
        }
    }

    private static Deadline getDeadline(String arguments) throws WodanException {
        String[] deadlineParts = arguments.split("\\s+/by(?:\\s+|$)", 2);
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
        rejectFileDelimiter(description);
        rejectFileDelimiter(by);
        Deadline deadline = new Deadline(description, by);
        return deadline;
    }

    private static Event getEvent(String arguments) throws WodanException {
        String[] fromParts = arguments.split("\\s+/from(?:\\s+|$)", 2);
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

        String rest = fromParts[1].trim();
        String from;
        String to;
        boolean hasTo;
        if (rest.equals("/to") || rest.startsWith("/to ") || rest.startsWith("/to\t")) {
            from = "";
            hasTo = true;
            to = rest.substring("/to".length()).trim();
        } else {
            String[] toParts = rest.split("\\s+/to(?:\\s+|$)", 2);
            from = toParts[0].trim();
            hasTo = toParts.length >= 2;
            to = hasTo ? toParts[1].trim() : "";
        }
        if (from.isEmpty()) {
            throw new WodanException(
                    "The ravens need a start time after /from. Try: event meeting /from Mon 2pm /to 4pm");
        }
        if (!hasTo) {
            throw new WodanException(
                    "An event must include /to <end>. Try: event meeting /from Mon 2pm /to 4pm");
        }
        if (to.isEmpty()) {
            throw new WodanException(
                    "The ravens need an end time after /to. Try: event meeting /from Mon 2pm /to 4pm");
        }
        rejectFileDelimiter(description);
        rejectFileDelimiter(from);
        rejectFileDelimiter(to);
        return new Event(description, from, to);
    }

    /**
     * Rejects values that contain {@code |}, which is reserved as the save-file delimiter.
     *
     * @param value User-provided task text to check.
     * @throws WodanException If {@code value} contains {@code |}.
     */
    private static void rejectFileDelimiter(String value) throws WodanException {
        if (value.contains("|")) {
            throw new WodanException(
                    "A quest cannot contain '|'. The ravens use that mark in the save file.");
        }
    }

    /**
     * Prints {@code message} between the usual reply lines.
     *
     * @param line Horizontal rule used around chatbot replies.
     * @param message Text to show the user.
     */
    private static void printMessage(String line, String message) {
        System.out.println(line);
        System.out.println("     " + message);
        System.out.println(line);
    }

    private static int getTaskNumber(String arguments, String x, ArrayList<Task> tasks, String message) throws WodanException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new WodanException(
                    "'" + arguments.trim() + x);
        }
        if (tasks.isEmpty()) {
            throw new WodanException(
                    message);
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new WodanException(
                    "There is no quest " + taskNumber + ". The ravens watch over " + tasks.size()
                            + (tasks.size() == 1 ? " quest" : " quests")
                            + ". Try a number from 1 to " + tasks.size() + ".");
        }
        return taskNumber;
    }
}
