/**
 * Words the chatbot recognises as commands.
 */
public enum Command {
    TODO, DEADLINE, EVENT, LIST,
    MARK, UNMARK, DELETE, ON, BYE;

    /**
     * Returns the command matching {@code word}, ignoring case.
     *
     * @param word First token of the user input.
     * @return The matching command.
     * @throws WodanException If {@code word} is not a known command.
     */
    public static Command parse(String word) throws WodanException {
        try {
            return Command.valueOf(word.toUpperCase());
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
