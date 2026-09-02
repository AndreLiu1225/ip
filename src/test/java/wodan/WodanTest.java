package wodan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    }

    @Test
    public void getResponse_bye_setsExit() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        String reply = wodan.getResponse("bye");
        assertEquals("So it is written. Farewell, wanderer.", reply);
        assertTrue(wodan.isExit());
    }

    @Test
    public void getGreeting_noSaveFile_welcomeOnly() {
        Wodan wodan = new Wodan(tempDir.resolve("wodan.txt").toString());
        assertEquals(
                "Hail, wanderer. Wodan is listening.\nWhat is your command?",
                wodan.getGreeting());
    }
}
