/**
 * Deletes a task from the list and saves.
 */
public class DeleteCommand extends Command {
    private final String arguments;

    /**
     * Creates a delete command from the text after {@code delete}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseDeleteNumber(arguments, tasks);
        Task removed = tasks.delete(taskNumber - 1);
        storage.save(tasks.getTasks());
        ui.show("     Noted. I've removed this task:");
        ui.show("       " + removed.toString());
        ui.show("     Now you have " + tasks.size() + " tasks in the list.");
    }
}
