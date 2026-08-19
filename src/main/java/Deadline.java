public class Deadline extends Task {
    protected String deadline;

    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public void markAsDone() {
        super.isDone = true;
    }

    @Override
    public void markAsUndone() {
        super.isDone = false;
    }

    @Override
    public String getStatusIcon() {
        return super.isDone ? "X" : " ";
    }

    @Override
    public String getDescription() {
        return super.description;
    }

    @Override
    public String toString() {
        return String.format("[D][%s] %s (by: %s)", getStatusIcon(), getDescription(), deadline);
    }

    public String getDeadline() {
        return this.deadline;
    }
}
