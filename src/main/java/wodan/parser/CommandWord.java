package wodan.parser;

import wodan.WodanException;

/**
 * Words the chatbot recognizes as the first token of a command line.
 */
public enum CommandWord {
    /** Add a todo task. */
    TODO,
    /** Add a deadline task. */
    DEADLINE,
    /** Add an event task. */
    EVENT,
    /** Show all tasks. */
    LIST,
    /** Mark a task as done. */
    MARK,
    /** Mark a task as not done. */
    UNMARK,
    /** Remove a task from the list. */
    DELETE,
    /** Show tasks that occur on a date. */
    ON,
    /** Find tasks by a keyword in the description. */
    FIND,
    /** Stop the chatbot. */
    BYE;

    /**
     * Returns the command word matching {@code word}, ignoring case.
     *
     * @param word First token of the user input.
     * @return The matching command word.
     * @throws WodanException If {@code word} is not a known command.
     */
    public static CommandWord parse(String word) throws WodanException {
        try {
            return CommandWord.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WodanException(unknownCommandMessage());
        }
    }

    /**
     * Returns the message used when the user types an unknown command.
     *
     * @return The unknown-command error text.
     */
    public static String unknownCommandMessage() {
        return "That rune is unknown. Speak "
                + joinWithOr("todo", "deadline", "event", "list", "mark",
                        "unmark", "delete", "on", "find", "bye")
                + ".";
    }

    /**
     * Returns {@code words} joined with commas, with {@code or} before the last word.
     *
     * @param words Words to join. Must contain at least one word.
     * @return A spoken list such as {@code todo, deadline, or bye}.
     */
    private static String joinWithOr(String... words) {
        if (words.length == 1) {
            return words[0];
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            result.append(words[i]);
            result.append(", ");
        }
        result.append("or ");
        result.append(words[words.length - 1]);
        return result.toString();
    }
}
