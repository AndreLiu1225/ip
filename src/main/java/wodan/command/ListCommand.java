package wodan.command;

import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Shows every task in the list, numbered from 1.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that lists every task.
     */
    public ListCommand() {
    }

    /**
     * Shows every task in {@code tasks}, numbered from 1.
     *
     * @param tasks The task list to show.
     * @param ui The user interface for the reply.
     * @param storage Unused.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show("    The ravens have given these quests.\n");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showNumberedTask(i + 1, tasks.get(i));
        }
    }
}
