/**
 * Words the chatbot recognises as the first token of a command line.
 */
public enum CommandWord {
    TODO, DEADLINE, EVENT, LIST,
    MARK, UNMARK, DELETE, ON, BYE;

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
     */
    public static String unknownCommandMessage() {
        return "That rune is unknown. Speak todo, deadline, event, list, mark, unmark, delete, on, or bye.";
    }
}
