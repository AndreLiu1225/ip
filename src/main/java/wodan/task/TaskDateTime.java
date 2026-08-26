package wodan.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import wodan.WodanException;

/**
 * A calendar date, optionally with a time of day, stored as a {@link LocalDateTime}.
 */
public class TaskDateTime {
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HH:mm")
                .withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("d/M/uuuu")
                .withResolverStyle(ResolverStyle.STRICT)
    };
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter STORAGE_DATE_TIME =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");

    public static final String HINT = "2026-08-26 or 08/26/2019 1800";

    private final LocalDateTime dateTime;
    private final boolean hasTime;

    /**
     * Creates a date-time value.
     *
     * @param dateTime Date and time to store. Midnight if {@code hasTime} is false.
     * @param hasTime Whether a time of day was specified.
     */
    public TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
    }

    /**
     * Returns a parsed date-time from user input.
     *
     * @param text Raw date or date-time text.
     * @return The parsed value.
     * @throws WodanException If {@code text} is not a supported date or date-time.
     */
    public static TaskDateTime parse(String text) throws WodanException {
        TaskDateTime parsed = tryParse(text);
        if (parsed == null) {
            throw new WodanException(
                    "The ravens cannot read '" + text + "'. Try: " + HINT);
        }
        return parsed;
    }

    /**
     * Returns a parsed date-time from a save-file field, or {@code null} if it is invalid.
     *
     * @param text Stored date or date-time text.
     * @return The parsed value, or {@code null} if {@code text} cannot be parsed.
     */
    public static TaskDateTime parseStorage(String text) {
        return tryParse(text);
    }

    /**
     * Returns {@code date} formatted as {@code MMM dd yyyy}.
     *
     * @param date Date to format.
     * @return Display text such as {@code Oct 15 2019}.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns this value as display text, with time only if one was given.
     */
    public String toDisplayString() {
        if (hasTime) {
            return dateTime.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
        }
        return dateTime.toLocalDate().format(DISPLAY_DATE);
    }

    /**
     * Returns this value encoded for the save file.
     */
    public String toStorageString() {
        if (hasTime) {
            return dateTime.format(STORAGE_DATE_TIME);
        }
        return dateTime.toLocalDate().toString();
    }

    /**
     * Returns the calendar date of this value.
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Returns the stored date-time.
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }

    /**
     * Returns whether a time of day was specified.
     */
    public boolean hasTime() {
        return hasTime;
    }

    private static TaskDateTime tryParse(String text) {
        String trimmed = text.trim();
        if (trimmed.contains("T")) {
            try {
                return new TaskDateTime(LocalDateTime.parse(trimmed), true);
            } catch (DateTimeParseException e) {
                try {
                    return new TaskDateTime(LocalDateTime.parse(trimmed, STORAGE_DATE_TIME), true);
                } catch (DateTimeParseException e2) {
                    return null;
                }
            }
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return new TaskDateTime(LocalDateTime.parse(trimmed, formatter), true);
            } catch (DateTimeParseException e) {
                // Try the next pattern.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmed, formatter);
                return new TaskDateTime(date.atStartOfDay(), false);
            } catch (DateTimeParseException e) {
                // Try the next pattern.
            }
        }
        return null;
    }
}
