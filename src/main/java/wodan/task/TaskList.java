package wodan.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The in-memory list of tasks, with operations to add, delete, and look up tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the given tasks, typically those loaded from storage.
     *
     * @param loaded Tasks to start with.
     */
    public TaskList(ArrayList<Task> loaded) {
        this.tasks = new ArrayList<>(loaded);
    }

    /**
     * Adds each of {@code toAdd} to the end of the list, in the given order.
     *
     * @param toAdd Tasks to append.
     */
    public void add(Task... toAdd) {
        for (Task task : toAdd) {
            tasks.add(task);
        }
    }

    /**
     * Removes and returns the task at 0-based {@code index}.
     *
     * @param index Position in the list, starting from 0.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at 0-based {@code index}.
     *
     * @param index Position in the list, starting from 0.
     * @return The task at that position.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return How many tasks are stored.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns {@code true} if the list has no tasks.
     *
     * @return Whether the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks in list order, for writing to the save file.
     * Callers must not add or remove elements through this list.
     *
     * @return The underlying task list.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the 1-based list numbers of tasks that occur on {@code date}.
     * The numbers match those shown by {@code list}.
     *
     * @param date Calendar date to match.
     * @return 1-based numbers of matching tasks, in list order.
     */
    public ArrayList<Integer> taskNumbersOn(LocalDate date) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                numbers.add(i + 1);
            }
        }
        return numbers;
    }

    /**
     * Returns the 1-based list numbers of tasks whose description contains {@code keyword}.
     * Matching ignores letter case. The numbers match those shown by {@code list}.
     *
     * @param keyword Text to search for in each description.
     * @return 1-based numbers of matching tasks, in list order.
     */
    public ArrayList<Integer> taskNumbersMatching(String keyword) {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).hasDescriptionContaining(keyword)) {
                numbers.add(i + 1);
            }
        }
        return numbers;
    }
}
