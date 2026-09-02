package wodan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wodan.WodanException;
import wodan.command.AddCommand;
import wodan.command.DeleteCommand;
import wodan.command.ExitCommand;
import wodan.command.FindCommand;
import wodan.command.ListCommand;
import wodan.command.OnCommand;
import wodan.storage.Storage;
import wodan.task.Deadline;
import wodan.task.Event;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.task.Todo;
import wodan.ui.Ui;

/**
 * Unit tests for {@link Parser}, focusing on command interpretation and validation.
 */
public class ParserTest {
    @TempDir
    Path tempDir;

    @Test
    public void parse_todoWithDescription_addCommandWithTodo() throws WodanException {
        Task task = addedTask("todo borrow book");
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parse_deadlineWithIsoDate_addCommandWithDeadline() throws WodanException {
        Task task = addedTask("deadline return book /by 2019-12-02");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 02 2019)", task.toString());
    }

    @Test
    public void parse_eventWithFromAndTo_addCommandWithEvent() throws WodanException {
        Task task = addedTask("event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)",
                task.toString());
    }

    @Test
    public void parse_listAndBye_matchingCommandTypes() throws WodanException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ExitCommand.class, Parser.parse("BYE"));
        assertTrue(Parser.parse("bye").isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_deleteOnAndFind_matchingCommandTypes() throws WodanException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_emptyLine_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("   "));
        assertEquals(
                "Silence is not a command. Speak todo, deadline, event, list, mark, "
                        + "unmark, delete, on, find, or bye.",
                ex.getMessage());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("blah"));
        assertEquals(CommandWord.unknownCommandMessage(), ex.getMessage());
    }

    @Test
    public void parse_todoMissingDescription_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("todo"));
        assertEquals("A todo needs a quest name. Try: todo borrow book", ex.getMessage());
    }

    @Test
    public void parse_todoWithPipe_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse("todo a | b"));
        assertEquals(
                "A quest cannot contain '|'. The ravens use that mark in the save file.",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineMissingBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline return book"));
        assertEquals(
                "A deadline must include /by <when>. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_deadlineDescriptionAfterBy_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "deadline /by 2019-12-02"));
        assertEquals(
                "A deadline needs a quest name before /by. Try: deadline return book /by 2019-12-02",
                ex.getMessage());
    }

    @Test
    public void parse_eventEndsBeforeStart_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
        assertEquals("An event cannot end before it starts.", ex.getMessage());
    }

    @Test
    public void parse_eventMissingTo_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "event meeting /from 2019-12-02 1400"));
        assertEquals(
                "An event must include /to <end>. "
                        + "Try: event meeting /from 2019-12-02 1400 /to 2019-12-02 1600",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_validIndex_returnsNumber() throws WodanException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        assertEquals(2, Parser.parseDeleteNumber("2", tasks));
    }

    @Test
    public void parseDeleteNumber_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "1", tasks));
        assertEquals(
                "There are no quests to delete yet. Add one with todo, deadline, or event.",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_outOfRange_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "3", tasks));
        assertEquals(
                "There is no quest 3. The ravens watch over 1 quest. Try a number from 1 to 1.",
                ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_notANumber_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "first", tasks));
        assertEquals("'first' is not a quest number. Try: delete 1", ex.getMessage());
    }

    @Test
    public void parseDeleteNumber_missingNumber_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseDeleteNumber(
                "", new TaskList()));
        assertEquals("Which quest is too burdensome? Try: delete 1", ex.getMessage());
    }

    @Test
    public void parseMarkNumber_missingNumber_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseMarkNumber(
                "  ", new TaskList()));
        assertEquals("Which quest should the ravens mark? Try: mark 1", ex.getMessage());
    }

    @Test
    public void parseOnDate_isoDate_returnsLocalDate() throws WodanException {
        assertEquals(java.time.LocalDate.of(2019, 12, 2), Parser.parseOnDate("2019-12-02"));
    }

    @Test
    public void parseOnDate_missingDate_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseOnDate(""));
        assertEquals("Which day should the ravens search? Try: on 2019-12-02", ex.getMessage());
    }

    @Test
    public void parseFindKeyword_trimmedText_returnsKeyword() throws WodanException {
        assertEquals("book", Parser.parseFindKeyword("  book  "));
        assertEquals("return book", Parser.parseFindKeyword("return book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parseFindKeyword(""));
        assertEquals("Which word should the ravens seek? Try: find book", ex.getMessage());
    }

    private Task addedTask(String command) throws WodanException {
        TaskList tasks = new TaskList();
        Parser.parse(command).execute(tasks, new Ui(),
                new Storage(tempDir.resolve("wodan.txt").toString()));
        assertInstanceOf(AddCommand.class, Parser.parse(command));
        return tasks.get(0);
    }
}
