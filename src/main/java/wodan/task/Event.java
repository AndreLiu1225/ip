package wodan.task;

import java.time.LocalDate;

/**
 * A task that starts and ends at given times.
 */
public class Event extends Task {
    /** When this event starts. */
    protected TaskDateTime startAt;
    /** When this event ends. */
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
        assert startAt != null && endAt != null : "Event needs a start and an end";
        assert !endAt.toLocalDateTime().isBefore(startAt.toLocalDateTime())
                : "Event end cannot be before start";
        this.startAt = startAt;
        this.endAt = endAt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsDone() {
        super.isDone = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsUndone() {
        super.isDone = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusIcon() {
        return super.isDone ? "X" : " ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return super.description;
    }

    /**
     * Returns this event as {@code [E][status] description (from: start to: end)}.
     *
     * @return Display text for list replies.
     */
    @Override
    public String toString() {
        return String.format("[E][%s] %s (from: %s to: %s)",
                getStatusIcon(), getDescription(),
                startAt.toDisplayString(), endAt.toDisplayString());
    }

    /**
     * Returns when this event starts.
     *
     * @return The start date or date-time.
     */
    public TaskDateTime getStartAt() {
        return this.startAt;
    }

    /**
     * Returns when this event ends.
     *
     * @return The end date or date-time.
     */
    public TaskDateTime getEndAt() {
        return this.endAt;
    }

    /**
     * Returns {@code true} if {@code date} is between the start and end dates, inclusive.
     *
     * @param date Date to check.
     * @return Whether this event falls on {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startAt.toLocalDate();
        LocalDate endDate = endAt.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Returns this event as {@code E | 0|1 | description | start | end}.
     *
     * @return One save-file line.
     */
    @Override
    public String toStorageString() {
        return "E | " + (super.isDone ? "1" : "0") + " | " + super.description
                + " | " + startAt.toStorageString() + " | " + endAt.toStorageString();
    }
}
