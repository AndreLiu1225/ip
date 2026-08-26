package wodan.task;

/**
 * A todo task with no date or time attached.
 */
public class Todo extends Task {

    public Todo(String description) {
        super(description);
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
        return String.format("[T][%s] %s", getStatusIcon(), getDescription());
    }

    @Override
    public String toStorageString() {
        return "T | " + (super.isDone ? "1" : "0") + " | " + super.description;
    }
}
