public enum Command {
    TODO, DEADLINE, EVENT, LIST,
    MARK, UNMARK, DELETE, BYE;

    public static Command parse(String word) throws WodanException {
        try {
            return Command.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WodanException(
                    "That rune is unknown. Speak todo, deadline, event, list, mark, unmark, delete, or bye."
            );
        }
    }
}
