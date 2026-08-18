/**
 * Entry point for the Wodan chatbot.
 * Prints a few ASCII-art banner variations for the mixed-case name "Wodan".
 */
public class Wodan {
    public static void main(String[] args) {
        String banner = " __          __       _\n"
                + " \\ \\        / /      | |\n"
                + "  \\ \\  /\\  / /__   __| | __ _ _ __\n"
                + "   \\ \\/  \\/ / _ \\ / _` |/ _` | '_ \\\n"
                + "    \\  /\\  / (_) | (_| | (_| | | | |\n"
                + "     \\/  \\/ \\___/ \\__,_|\\__,_|_| |_|\n";

        String message = String.format("""
                ____________________________________________________________
                
                %s
                Hello! I'm Wodan.
                
                What can I do for you?
                
                ____________________________________________________________
                
                Bye. Hope to see you again soon!
                
                ____________________________________________________________
                """, banner);

        System.out.println(message);
    }
}
