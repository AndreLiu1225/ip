import java.time.LocalDate;

/**
 * A task that must be done by a given deadline.
 */
public class Deadline extends Task {
    protected TaskDateTime dueAt;

    /**
     * Creates a deadline with the given due date or date-time.
     *
     * @param description What must be done.
     * @param dueAt When it is due.
     */
    public Deadline(String description, TaskDateTime dueAt) {
        super(description);
        this.dueAt = dueAt;
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
        return String.format("[D][%s] %s (by: %s)",
                getStatusIcon(), getDescription(), dueAt.toDisplayString());
    }

    public TaskDateTime getDueAt() {
        return this.dueAt;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dueAt.toLocalDate().equals(date);
    }

    @Override
    public String toStorageString() {
        return "D | " + (super.isDone ? "1" : "0") + " | " + super.description
                + " | " + dueAt.toStorageString();
    }
}
