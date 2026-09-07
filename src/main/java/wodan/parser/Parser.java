package wodan.parser;

import java.time.LocalDate;

import wodan.WodanException;
import wodan.command.AddCommand;
import wodan.command.Command;
import wodan.command.DeleteCommand;
import wodan.command.ExitCommand;
import wodan.command.FindCommand;
import wodan.command.ListCommand;
import wodan.command.MarkCommand;
import wodan.command.OnCommand;
import wodan.command.UnmarkCommand;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.TaskDateTime;
import wodan.task.TaskList;
import wodan.task.Todo;

/**
 * Interprets a user command line: the command word, its arguments, and task details.
 */
public class Parser {
    /**
     * Prevents instantiation; command parsing is done through static methods.
     */
    private Parser() {
    }

    /**
     * Returns a command object for {@code fullCommand}.
     *
     * @param fullCommand One line typed by the user.
     * @return The command to execute.
     * @throws WodanException If the line is empty, unknown, or has invalid arguments.
     */
    public static Command parse(String fullCommand) throws WodanException {
        String[] words = splitCommandLine(fullCommand);
        CommandWord commandWord = CommandWord.parse(words[0]);
        String arguments = words.length > 1 ? words[1] : "";
        switch (commandWord) {
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(arguments);
            case UNMARK:
                return new UnmarkCommand(arguments);
            case DELETE:
                return new DeleteCommand(arguments);
            case ON:
                return new OnCommand(arguments);
            case FIND:
                return new FindCommand(arguments);
            case BYE:
                return new ExitCommand();
            default:
                throw new WodanException(CommandWord.unknownCommandMessage());
        }
    }

    /**
     * Splits {@code fullCommand} into the command word and the remaining argument text.
     *
     * @param fullCommand One line typed by the user.
     * @return The first word, then the rest of the line if any.
     * @throws WodanException If the line is empty.
     */
    private static String[] splitCommandLine(String fullCommand) throws WodanException {
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new WodanException(
                    "Silence is not a command. Speak todo, deadline, event, list, mark, "
                            + "unmark, delete, on, find, or bye.");
        }
        return trimmed.split(" ", 2);
    }

    /**
     * Returns a todo built from {@code arguments}.
     *
     * @param arguments Text after the {@code todo} command word.
     * @return The todo to add.
     * @throws WodanException If the description is missing or contains {@code |}.
     */
    private static Todo parseTodo(String arguments) throws WodanException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new WodanException(
                    "A todo needs a quest name. Try: todo borrow book");
        }
        rejectFileDelimiter(description);
        return new Todo(description);
    }

    /**
     * Returns a deadline built from {@code arguments}.
     *
     * @param arguments Text after the {@code deadline} command word, including {@code /by}.
     * @return The deadline to add.
     * @throws WodanException If the description, {@code /by} time, or date is invalid.
     */
    private static Deadline parseDeadline(String arguments) throws WodanException {
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

    /**
     * Returns an event built from {@code arguments}.
     *
     * @param arguments Text after the {@code event} command word, including {@code /from} and {@code /to}.
     * @return The event to add.
     * @throws WodanException If the description, times, or date range is invalid.
     */
    private static Event parseEvent(String arguments) throws WodanException {
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

    /**
     * Returns the 1-based list number for a {@code delete} command.
     *
     * @param arguments Text after the {@code delete} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseDeleteNumber(String arguments, TaskList tasks) throws WodanException {
        return parseTaskNumber(arguments, tasks,
                "Which quest is too burdensome? Try: delete 1",
                "' is not a quest number. Try: delete 1",
                "There are no quests to delete yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the 1-based list number for a {@code mark} command.
     *
     * @param arguments Text after the {@code mark} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseMarkNumber(String arguments, TaskList tasks) throws WodanException {
        return parseTaskNumber(arguments, tasks,
                "Which quest should the ravens mark? Try: mark 1",
                "' is not a quest number. Try: mark 1",
                "There are no quests to mark yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the 1-based list number for an {@code unmark} command.
     *
     * @param arguments Text after the {@code unmark} command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    public static int parseUnmarkNumber(String arguments, TaskList tasks) throws WodanException {
        return parseTaskNumber(arguments, tasks,
                "Which quest should the ravens unmark? Try: unmark 1",
                "' is not a quest number. Try: unmark 1",
                "There are no quests to unmark yet. Add one with todo, deadline, or event.");
    }

    /**
     * Returns the calendar date for an {@code on} command.
     *
     * @param arguments Text after the {@code on} command word.
     * @return The date to search.
     * @throws WodanException If the date is missing or cannot be parsed.
     */
    public static LocalDate parseOnDate(String arguments) throws WodanException {
        if (arguments.trim().isEmpty()) {
            throw new WodanException(
                    "Which day should the ravens search? Try: on 2019-12-02");
        }
        return TaskDateTime.parse(arguments.trim()).toLocalDate();
    }

    /**
     * Returns the keyword for a {@code find} command.
     *
     * @param arguments Text after the {@code find} command word.
     * @return The trimmed keyword.
     * @throws WodanException If the keyword is missing.
     */
    public static String parseFindKeyword(String arguments) throws WodanException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new WodanException(
                    "Which word should the ravens seek? Try: find book");
        }
        return keyword;
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
     * Returns a 1-based task number parsed from {@code arguments} that exists in {@code tasks}.
     *
     * @param arguments Text after the command word.
     * @param tasks Current task list, used to check that the number is in range.
     * @param missingMessage Error if {@code arguments} is blank.
     * @param notANumberSuffix Error suffix if {@code arguments} is not an integer.
     * @param emptyListMessage Error if {@code tasks} has no items.
     * @return A 1-based task number that exists in {@code tasks}.
     * @throws WodanException If the number is missing, not an integer, or out of range.
     */
    private static int parseTaskNumber(String arguments, TaskList tasks, String missingMessage,
            String notANumberSuffix, String emptyListMessage) throws WodanException {
        if (arguments.trim().isEmpty()) {
            throw new WodanException(missingMessage);
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new WodanException(
                    "'" + arguments.trim() + notANumberSuffix);
        }
        if (tasks.isEmpty()) {
            throw new WodanException(emptyListMessage);
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
