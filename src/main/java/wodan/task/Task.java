package wodan.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A task that the user can mark as done or not done.
 */
public abstract class Task {
    /** What the user asked to do. */
    protected String description;
    /** Whether this task has been marked done. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description What the task is.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public abstract void markAsDone();

    /**
     * Marks this task as not done.
     */
    public abstract void markAsUndone();

    /**
     * Returns the status icon for this task.
     *
     * @return {@code X} if done, or a space if not done.
     */
    public abstract String getStatusIcon();

    /**
     * Returns the description of this task.
     *
     * @return What the task is.
     */
    public abstract String getDescription();

    /**
     * Returns this task as the user sees it in list replies.
     *
     * @return Display text including type, status, and details.
     */
    public abstract String toString();

    /**
     * Returns this task encoded as one line for the save file.
     *
     * @return A pipe-separated line describing the task type, done status, and details.
     */
    public abstract String toStorageString();

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

    /**
     * Returns {@code true} if this task's description contains {@code keyword}.
     * Matching ignores letter case and looks only at the description, not dates.
     *
     * @param keyword Text to search for.
     * @return Whether the description contains {@code keyword}.
     */
    public boolean hasDescriptionContaining(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }
}