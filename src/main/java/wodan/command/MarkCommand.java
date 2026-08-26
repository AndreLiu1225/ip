package wodan.command;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Marks a task as done and saves.
 */
public class MarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a mark command from the text after {@code mark}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public MarkCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseMarkNumber(arguments, tasks);
        Task currTask = tasks.get(taskNumber - 1);
        currTask.markAsDone();
        storage.save(tasks.getTasks());
        ui.show("    One less burden to carry.");
        ui.show("     " + currTask.toString());
        ui.show("");
    }
}
