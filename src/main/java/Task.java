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
}