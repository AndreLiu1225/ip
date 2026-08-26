import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Entry point for the Wodan chatbot.
 * Saves the task list to {@code data/wodan.txt} (relative to the working directory)
 * whenever it changes.
 * Loads the task list from that file when the chatbot starts.
 */
public class Wodan {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage();
        ArrayList<Task> tasks = new ArrayList<Task>();
        String startMessage = null;
        try {
            tasks = storage.load();
            startMessage = storage.getLoadWarning();
        } catch (WodanException e) {
            startMessage = e.getMessage();
        }

        ui.showWelcome();
        if (startMessage != null) {
            ui.showError(startMessage);
        }

        label:
        while (true) {
            if (!ui.hasCommand()) {
                break label;
            }
            String command = ui.readCommand();
            try {
                if (command.trim().isEmpty()) {
                    throw new WodanException(
                            "Silence is not a command. Speak todo, deadline, event, list, mark, unmark, delete, on, or bye.");
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
                        ui.showLine();
                        ui.show("     Noted. I've removed this task:");
                        ui.show("       " + tbr.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
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
                        ui.showLine();
                        ui.show("    The ravens retract their approval.");
                        ui.show("     " + currTask.toString());
                        ui.showLine();
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
                        ui.showLine();
                        ui.show("    One less burden to carry.");
                        ui.show("     " + currTask.toString());
                        ui.show("");
                        ui.showLine();
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
                        ui.show("    You have accepted the following quest:");
                        ui.show("        " + todo.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case DEADLINE: {
                        Deadline deadline = getDeadline(arguments);
                        tasks.add(deadline);
                        storage.save(tasks);
                        ui.show("    You have accepted the following quest:");
                        ui.show("        " + deadline.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case EVENT: {
                        Event event = getEvent(arguments);
                        tasks.add(event);
                        storage.save(tasks);
                        ui.show("    You have accepted the following quest:");
                        ui.show("        " + event.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case BYE:
                        ui.showGoodbye();
                        break label;
                    case LIST:
                        ui.showLine();
                        ui.show("    The ravens have given these quests.\n");
                        for (int i = 0; i < tasks.size(); i++) {
                            ui.showNumberedTask(i + 1, tasks.get(i));
                        }
                        ui.showLine();
                        break;
                    case ON: {
                        printTasksOnDate(ui, tasks, arguments);
                        break;
                    }
                    default:
                        throw new WodanException(Command.unknownCommandMessage());
                }
            } catch (WodanException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    private static Deadline getDeadline(String arguments) throws WodanException {
        String[] deadlineParts = arguments.split("\\s+/by(?:\\s+|$)", 2);
        String description = deadlineParts[0].trim();
        String by = deadlineParts.length > 1 ? deadlineParts[1].trim() : "";
        if (description.startsWith("/by")) {
            throw new WodanException(
                    "A deadline needs a quest name before /by. Try: deadline return book /by 2019-12-02");
        }
        if (description.isEmpty()) {
            throw new WodanException(
                    "A deadline needs a quest name and /by <when>. Try: deadline return book /by 2019-12-02");
        }
        if (deadlineParts.length < 2) {
            throw new WodanException(
                    "A deadline must include /by <when>. Try: deadline return book /by 2019-12-02");
        }
        if (by.isEmpty()) {
            throw new WodanException(
                    "The ravens need a time after /by. Try: deadline return book /by 2019-12-02");
        }
        rejectFileDelimiter(description);
        rejectFileDelimiter(by);
        TaskDateTime dueAt = TaskDateTime.parse(by);
        return new Deadline(description, dueAt);
    }

    private static Event getEvent(String arguments) throws WodanException {
        String[] fromParts = arguments.split("\\s+/from(?:\\s+|$)", 2);
        String description = fromParts[0].trim();
        if (description.startsWith("/from")) {
            throw new WodanException(
                    "An event needs a quest name before /from. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        if (description.isEmpty()) {
            throw new WodanException(
                    "An event needs a name, /from <start>, and /to <end>. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        if (fromParts.length < 2) {
            throw new WodanException(
                    "An event must include /from <start> and /to <end>. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
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
                    "The ravens need a start time after /from. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        if (!hasTo) {
            throw new WodanException(
                    "An event must include /to <end>. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        if (to.isEmpty()) {
            throw new WodanException(
                    "The ravens need an end time after /to. "
                            + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        rejectFileDelimiter(description);
        rejectFileDelimiter(from);
        rejectFileDelimiter(to);
        TaskDateTime startAt = TaskDateTime.parse(from);
        TaskDateTime endAt = TaskDateTime.parse(to);
        if (endAt.toLocalDateTime().isBefore(startAt.toLocalDateTime())) {
            throw new WodanException("An event cannot end before it starts.");
        }
        return new Event(description, startAt, endAt);
    }

    private static void printTasksOnDate(Ui ui, ArrayList<Task> tasks, String arguments)
            throws WodanException {
        if (arguments.trim().isEmpty()) {
            throw new WodanException(
                    "Which day should the ravens search? Try: on 2019-12-02");
        }
        LocalDate date = TaskDateTime.parse(arguments.trim()).toLocalDate();
        String displayDate = TaskDateTime.formatDate(date);
        ui.showLine();
        boolean isFound = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!isFound) {
                    ui.show("    The ravens found these quests on " + displayDate + ".\n");
                    isFound = true;
                }
                ui.showNumberedTask(i + 1, tasks.get(i));
            }
        }
        if (!isFound) {
            ui.show("    The ravens found no quests on " + displayDate + ".");
        }
        ui.showLine();
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

    private static int getTaskNumber(String arguments, String x, ArrayList<Task> tasks, String message)
            throws WodanException {
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
