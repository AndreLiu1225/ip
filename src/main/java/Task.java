/**
 * A task that the user can mark as done or not done.
 */
abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    abstract public void markAsDone();

    abstract public void markAsUndone();

    abstract public String getStatusIcon();

    abstract public String getDescription();

    abstract public String toString();

    /**
     * Returns this task encoded as one line for the save file.
     *
     * @return A pipe-separated line describing the task type, done status, and details.
     */
    abstract public String toStorageString();
}