package wodan;

/**
 * A chatbot-specific error, such as an empty description or an unknown command.
 */
public class WodanException extends Exception {
    public WodanException(String message) {
        super(message);
    }
}
