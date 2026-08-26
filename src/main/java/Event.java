import java.time.LocalDate;

/**
 * A task that starts and ends at given times.
 */
public class Event extends Task {
    protected TaskDateTime startAt;
    protected TaskDateTime endAt;

    /**
     * Creates an event with the given start and end date-times.
     *
     * @param description What the event is.
     * @param startAt When the event starts.
     * @param endAt When the event ends.
     */
    public Event(String description, TaskDateTime startAt, TaskDateTime endAt) {
        super(description);
        this.startAt = startAt;
        this.endAt = endAt;
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
        return String.format("[E][%s] %s (from: %s to: %s)",
                getStatusIcon(), getDescription(),
                startAt.toDisplayString(), endAt.toDisplayString());
    }

    public TaskDateTime getStartAt() {
        return this.startAt;
    }

    public TaskDateTime getEndAt() {
        return this.endAt;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startAt.toLocalDate();
        LocalDate endDate = endAt.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public String toStorageString() {
        return "E | " + (super.isDone ? "1" : "0") + " | " + super.description
                + " | " + startAt.toStorageString() + " | " + endAt.toStorageString();
    }
}
