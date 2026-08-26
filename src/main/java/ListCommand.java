/**
 * Shows every task in the list, numbered from 1.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show("    The ravens have given these quests.\n");
        for (int i = 0; i < tasks.size(); i++) {
            ui.showNumberedTask(i + 1, tasks.get(i));
        }
    }
}
