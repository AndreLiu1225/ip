/**
 * Adds a task (todo, deadline, or event) to the list and saves.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add {@code task}.
     *
     * @param task The task parsed from the user command.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.show("    You have accepted the following quest:");
        ui.show("        " + task.toString());
        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
    }
}
