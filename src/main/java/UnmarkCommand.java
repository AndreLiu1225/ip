/**
 * Marks a task as not done and saves.
 */
public class UnmarkCommand extends Command {
    private final String arguments;

    /**
     * Creates an unmark command from the text after {@code unmark}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public UnmarkCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseUnmarkNumber(arguments, tasks);
        Task currTask = tasks.get(taskNumber - 1);
        currTask.markAsUndone();
        storage.save(tasks.getTasks());
        ui.show("    The ravens retract their approval.");
        ui.show("     " + currTask.toString());
    }
}
