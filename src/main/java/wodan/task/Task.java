package wodan.task;

import java.time.LocalDate;

/**
 * A task that the user can mark as done or not done.
 */
public abstract class Task {
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

    /**
     * Returns {@code true} if this task occurs on {@code date}.
     * Todos never occur on a calendar date.
     *
     * @param date Date to check.
     * @return Whether this task falls on {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }
}