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
        TaskList tasks = new TaskList();
        String startMessage = null;
        try {
            tasks = new TaskList(storage.load());
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

    private static void printTasksOnDate(Ui ui, TaskList tasks, String arguments)
            throws WodanException {
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
