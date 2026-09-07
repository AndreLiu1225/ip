package wodan.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A task that the user can mark as done or not done.
 */
public abstract class Task {
    private static final String STATUS_DONE_ICON = "X";
    private static final String STATUS_NOT_DONE_ICON = " ";

    /** What the user asked to do. */
    private String description;
    /** Whether this task has been marked done. */
    private boolean isDone;

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
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return {@code X} if done, or a space if not done.
     */
    public String getStatusIcon() {
        return isDone ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
    }

    /**
     * Returns the description of this task.
     *
     * @return What the task is.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked done.
     *
     * @return {@code true} if done.
     */
    public boolean isDone() {
        return isDone;
    }

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
