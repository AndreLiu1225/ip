package wodan.task;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link TaskList} add, delete, and date lookup.
 */
public class TaskListTest {
    @Test
    public void addAndDelete_updatesSizeAndOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        Task removed = tasks.delete(0);
        assertEquals(first, removed);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    public void constructor_copyOfLoadedList_independentFromOriginal() {
        ArrayList<Task> loaded = new ArrayList<Task>();
        loaded.add(new Todo("keep"));
        TaskList tasks = new TaskList(loaded);
        loaded.add(new Todo("extra"));
        assertEquals(1, tasks.size());
        assertEquals("keep", tasks.get(0).getDescription());
    }

    @Test
    public void taskNumbersOn_mixedTasks_keepsOriginalListNumbers() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"));
        tasks.add(new Deadline("due that day", TaskDateTime.parse("2019-12-02")));
        tasks.add(new Deadline("due another day", TaskDateTime.parse("2019-12-03")));
        Event spanning = new Event("meet",
                TaskDateTime.parse("2019-12-01"),
                TaskDateTime.parse("2019-12-03"));
        tasks.add(spanning);

        ArrayList<Integer> numbers = tasks.taskNumbersOn(LocalDate.of(2019, 12, 2));
        assertEquals(2, numbers.size());
        assertEquals(Integer.valueOf(2), numbers.get(0));
        assertEquals(Integer.valueOf(4), numbers.get(1));
    }

    @Test
    public void taskNumbersOn_noMatches_emptyList() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"));
        tasks.add(new Deadline("due later", TaskDateTime.parse("2019-12-03")));
        assertTrue(tasks.taskNumbersOn(LocalDate.of(2019, 12, 2)).isEmpty());
    }
}
