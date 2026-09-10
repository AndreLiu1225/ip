package wodan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

/**
 * Unit tests for {@link Event} date-range matching and encoding.
 */
public class EventTest {
    @Test
    public void occursOn_inclusiveStartAndEnd_trueInsideRange() throws WodanException {
        Event event = new Event("camp",
                TaskDateTime.parse("2019-12-02"),
                TaskDateTime.parse("2019-12-04"));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 4)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void toStringAndStorage_unmarkedEvent_includesFromAndTo() throws WodanException {
        Event event = new Event("meeting",
                TaskDateTime.parse("2019-12-02 1400"),
                TaskDateTime.parse("2019-12-02 1600"));
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)",
                event.toString());
        assertEquals("E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00 | general",
                event.toStorageString());
    }
}
