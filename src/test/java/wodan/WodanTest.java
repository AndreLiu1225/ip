package wodan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for GUI-facing {@link Wodan} replies.
 */
public class WodanTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_todo_includesAcceptedQuest() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("todo borrow book");
        assertTrue(reply.contains("borrow book"));
        assertTrue(reply.contains("accepted the following quest"));
        assertFalse(wodan.isExit());
        assertFalse(wodan.wasError());
    }

    @Test
    public void getResponse_bye_setsExit() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("bye");
        assertEquals("So it is written. Farewell, wanderer.", reply);
        assertTrue(wodan.isExit());
        assertFalse(wodan.wasError());
    }

    @Test
    public void getResponse_duplicateTodo_setsError() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        wodan.getResponse("todo borrow book");
        String reply = wodan.getResponse("todo borrow book");
        assertTrue(wodan.wasError());
        assertTrue(reply.contains("already on the list"));
        assertFalse(wodan.isExit());
    }

    @Test
    public void getGreeting_noSaveFile_welcomeOnly() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertEquals(
                "Hail, wanderer. Wodan is listening.\nWhat is your command?",
                wodan.getGreeting());
    }

    @Test
    public void getLoadMessage_noSaveFile_null() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertNull(wodan.getLoadMessage());
    }

    @Test
    public void getResponse_unknownCommand_setsError() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("blah");
        assertTrue(wodan.wasError());
        assertTrue(reply.contains("That rune is unknown"));
        assertFalse(wodan.isExit());
    }

    @Test
    public void getResponse_validThenInvalid_errorFlagFollowsLastReply() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        wodan.getResponse("todo borrow book");
        assertFalse(wodan.wasError());
        wodan.getResponse("not-a-command");
        assertTrue(wodan.wasError());
        wodan.getResponse("list");
        assertFalse(wodan.wasError());
    }
}
