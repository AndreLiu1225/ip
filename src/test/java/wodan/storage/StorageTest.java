package wodan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wodan.WodanException;
import wodan.task.Deadline;
import wodan.task.Task;
import wodan.task.TaskDateTime;
import wodan.task.Todo;

/**
 * Unit tests for {@link Storage} load and save behavior.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_emptyListWithoutWarning() throws WodanException {
        Path file = tempDir.resolve("missing.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void saveThenLoad_roundTrip_preservesTodoAndDeadline() throws Exception {
        Path file = tempDir.resolve("nested").resolve("wodan.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));
        storage.save(original);

        ArrayList<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("[T][X] borrow book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertNull(storage.getLoadWarning());
    }

    @Test
    public void load_corruptLines_skipsThemAndWarns() throws Exception {
        Path file = tempDir.resolve("wodan.txt");
        Files.writeString(file,
                "T | 1 | keep me\nthis is garbage\nX | 0 | unknown\nT | 1 | also keep\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("keep me", loaded.get(0).getDescription());
        assertEquals("also keep", loaded.get(1).getDescription());
        assertEquals("The ravens skipped 2 corrupted quests in the save file.",
                storage.getLoadWarning());
    }

    @Test
    public void load_pathIsDirectory_exceptionThrown() throws Exception {
        Path dir = tempDir.resolve("not-a-file");
        Files.createDirectory(dir);
        Storage storage = new Storage(dir.toString());
        WodanException ex = assertThrows(WodanException.class, storage::load);
        assertEquals("The save path is not a file. The ravens could not recall the quests.",
                ex.getMessage());
    }
}
