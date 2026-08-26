import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Coordinates the chatbot: user interface, task list, and disk storage.
 * Saves the task list whenever it changes, and loads it when the chatbot starts.
 */
public class Wodan {
    private static final String DEFAULT_SAVE_PATH = Path.of("data", "wodan.txt").toString();

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startMessage;

    /**
     * Creates a chatbot that stores tasks at {@code filePath}.
     * If the file cannot be loaded, the task list starts empty and a message is shown at startup.
     *
     * @param filePath Save-file path, relative to the working directory if the path is relative.
     */
    public Wodan(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        String loadMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
            loadMessage = storage.getLoadWarning();
        } catch (WodanException e) {
            loadedTasks = new TaskList();
            loadMessage = e.getMessage();
        }
        tasks = loadedTasks;
        startMessage = loadMessage;
    }

    /**
     * Shows the greeting and then reads commands until the user says bye, or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (startMessage != null) {
            ui.showError(startMessage);
        }

        label:
        while (true) {
            if (!ui.hasCommand()) {
                break label;
            }
            String commandLine = ui.readCommand();
            try {
                Command command = Parser.parseCommand(commandLine);
                String arguments = Parser.parseArguments(commandLine);

                switch (command) {
                    case DELETE: {
                        int taskNumber = Parser.parseDeleteNumber(arguments, tasks);
                        Task tbr = tasks.delete(taskNumber - 1);
                        storage.save(tasks.getTasks());
                        ui.showLine();
                        ui.show("     Noted. I've removed this task:");
                        ui.show("       " + tbr.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case UNMARK: {
                        int taskNumber = Parser.parseUnmarkNumber(arguments, tasks);
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsUndone();
                        storage.save(tasks.getTasks());
                        ui.showLine();
                        ui.show("    The ravens retract their approval.");
                        ui.show("     " + currTask.toString());
                        ui.showLine();
                        break;
                    }
                    case MARK: {
                        int taskNumber = Parser.parseMarkNumber(arguments, tasks);
                        Task currTask = tasks.get(taskNumber - 1);
                        currTask.markAsDone();
                        storage.save(tasks.getTasks());
                        ui.showLine();
                        ui.show("    One less burden to carry.");
                        ui.show("     " + currTask.toString());
                        ui.show("");
                        ui.showLine();
                        break;
                    }
                    case TODO: {
                        Todo todo = Parser.parseTodo(arguments);
                        tasks.add(todo);
                        storage.save(tasks.getTasks());
                        ui.show("    You have accepted the following quest:");
                        ui.show("        " + todo.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case DEADLINE: {
                        Deadline deadline = Parser.parseDeadline(arguments);
                        tasks.add(deadline);
                        storage.save(tasks.getTasks());
                        ui.show("    You have accepted the following quest:");
                        ui.show("        " + deadline.toString());
                        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
                        ui.showLine();
                        break;
                    }
                    case EVENT: {
                        Event event = Parser.parseEvent(arguments);
                        tasks.add(event);
                        storage.save(tasks.getTasks());
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
                        printTasksOnDate(arguments);
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

    /**
     * Starts the chatbot using {@code data/wodan.txt} relative to the working directory.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new Wodan(DEFAULT_SAVE_PATH).run();
    }

    private void printTasksOnDate(String arguments) throws WodanException {
        LocalDate date = Parser.parseOnDate(arguments);
        String displayDate = TaskDateTime.formatDate(date);
        ui.showLine();
        ArrayList<Integer> numbers = tasks.taskNumbersOn(date);
        if (numbers.isEmpty()) {
            ui.show("    The ravens found no quests on " + displayDate + ".");
        } else {
            ui.show("    The ravens found these quests on " + displayDate + ".\n");
            for (int number : numbers) {
                ui.showNumberedTask(number, tasks.get(number - 1));
            }
        }
        ui.showLine();
    }
}
