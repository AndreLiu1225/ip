package wodan.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import wodan.WodanException;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.Task;
import wodan.task.TaskDateTime;
import wodan.task.Todo;

/**
 * Reads and writes the task list on the hard disk.
 * Invalid save-file lines are skipped so that readable tasks are not lost.
 * The save location is supplied by the caller, typically
 * {@code Path.of("data", "wodan.txt")} so it works on every operating system.
 */
public class Storage {
    private final Path filePath;
    private String loadWarning;

    /**
     * Creates storage at {@code filePath}, relative to the working directory if the path is relative.
     *
     * @param filePath Save-file path, for example {@code data/wodan.txt}.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
        this.loadWarning = null;
    }

    /**
     * Returns a warning about skipped save-file lines from the most recent load, or {@code null}.
     */
    public String getLoadWarning() {
        return loadWarning;
    }

    /**
     * Returns the tasks stored in the save file.
     * If the file does not exist or is empty, an empty list is returned.
     * Corrupted lines are skipped and recorded as a warning.
     *
     * @return Tasks loaded from disk, or an empty list if there is no save file.
     * @throws WodanException If the path exists but cannot be read as a file.
     */
    public ArrayList<Task> load() throws WodanException {
        loadWarning = null;
        ArrayList<Task> tasks = new ArrayList<Task>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(filePath)) {
            throw new WodanException(
                    "The save path is not a file. The ravens could not recall the quests.");
        }

        try {
            List<String> lines = new ArrayList<String>(
                    Files.readAllLines(filePath, StandardCharsets.UTF_8));
            stripBom(lines);
            int skipped = 0;
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task == null) {
                    skipped++;
                    continue;
                }
                tasks.add(task);
            }
            if (skipped == 1) {
                loadWarning = "The ravens skipped 1 corrupted quest in the save file.";
            } else if (skipped > 1) {
                loadWarning = "The ravens skipped " + skipped + " corrupted quests in the save file.";
            }
            return tasks;
        } catch (IOException e) {
            throw new WodanException("The ravens could not recall the quests.");
        }
    }

    /**
     * Writes the given tasks to the save file, creating the parent directory if it does not exist.
     *
     * @param tasks Tasks to persist.
     * @throws WodanException If the file cannot be created or written.
     */
    public void save(ArrayList<Task> tasks) throws WodanException {
        if (Files.exists(filePath) && !Files.isRegularFile(filePath)) {
            throw new WodanException(
                    "The save path is not a file. The ravens could not record the quests.");
        }

        try {
            createParentDirectory();

            ArrayList<String> lines = new ArrayList<String>();
            for (Task task : tasks) {
                lines.add(task.toStorageString());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new WodanException("The ravens could not record the quests.");
        }
    }

    /**
     * Creates the {@code data} folder if it does not already exist.
     *
     * @throws IOException If the folder cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * Returns the task encoded in {@code line}, or {@code null} if the line is not valid.
     */
    private Task parseTask(String line) {
        String[] parts = splitFields(line);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        String doneFlag = parts[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            return null;
        }
        boolean isDone = doneFlag.equals("1");
        String description = parts[2];
        if (description.isEmpty()) {
            return null;
        }

        Task task;
        switch (type) {
            case "T":
                if (parts.length != 3) {
                    return null;
                }
                task = new Todo(description);
                break;
            case "D":
                if (parts.length != 4 || parts[3].isEmpty()) {
                    return null;
                }
                TaskDateTime dueAt = TaskDateTime.parseStorage(parts[3]);
                if (dueAt == null) {
                    return null;
                }
                task = new Deadline(description, dueAt);
                break;
            case "E":
                if (parts.length != 5 || parts[3].isEmpty() || parts[4].isEmpty()) {
                    return null;
                }
                TaskDateTime startAt = TaskDateTime.parseStorage(parts[3]);
                TaskDateTime endAt = TaskDateTime.parseStorage(parts[4]);
                if (startAt == null || endAt == null) {
                    return null;
                }
                if (endAt.toLocalDateTime().isBefore(startAt.toLocalDateTime())) {
                    return null;
                }
                task = new Event(description, startAt, endAt);
                break;
            default:
                return null;
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a save-file line on {@code |} and trims each field.
     */
    private String[] splitFields(String line) {
        String[] rawParts = line.split("\\|", -1);
        String[] parts = new String[rawParts.length];
        for (int i = 0; i < rawParts.length; i++) {
            parts[i] = rawParts[i].trim();
        }
        return parts;
    }

    /**
     * Removes a UTF-8 BOM from the first line if present.
     */
    private void stripBom(List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        String first = lines.get(0);
        if (first.startsWith("\uFEFF")) {
            lines.set(0, first.substring(1));
        }
    }
}
