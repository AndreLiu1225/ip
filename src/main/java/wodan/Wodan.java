package wodan;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import wodan.command.Command;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Coordinates the chatbot: user interface, task list, and disk storage.
 * Saves the task list whenever it changes, and loads it when the chatbot starts.
 */
public class Wodan {
    private static final String DEFAULT_SAVE_PATH = Path.of("data", "wodan.txt").toString();

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startMessage;
    private boolean isExit;

    /**
     * Creates a chatbot that stores tasks in {@code data/wodan.txt}.
     */
    public Wodan() {
        this(DEFAULT_SAVE_PATH);
    }

    /**
     * Creates a chatbot that stores tasks at {@code filePath}.
     * If the file cannot be loaded, the task list starts empty and a message is shown at startup.
     *
     * @param filePath Save-file path, relative to the working directory if the path is relative.
     */
    public Wodan(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        String loadMessage = null;
        try {
            loadedTasks = new TaskList(storage.load());
            loadMessage = storage.getLoadWarning();
        } catch (WodanException e) {
            loadedTasks = new TaskList();
            loadMessage = e.getMessage();
        }
        tasks = loadedTasks;
        startMessage = loadMessage;
        isExit = false;
    }

    /**
     * Shows the greeting and then reads commands until the user says bye, or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (startMessage != null) {
            ui.showLine();
            ui.showError(startMessage);
            ui.showLine();
        }

        boolean shouldStop = false;
        while (!shouldStop) {
            if (!ui.hasCommand()) {
                break;
            }
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                shouldStop = command.isExit();
            } catch (WodanException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Returns the greeting shown when the graphical UI starts.
     *
     * @return Welcome text, plus a load warning if the save file could not be read cleanly.
     */
    public String getGreeting() {
        String greeting = Ui.getGreetingText();
        if (startMessage == null) {
            return greeting;
        }
        return greeting + "\n\n" + startMessage;
    }

    /**
     * Returns Wodan's reply to {@code input} for the graphical UI.
     * Uses the same parser and commands as the text UI.
     *
     * @param input Command typed by the user.
     * @return Reply text, without the console divider lines.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            Ui replyUi = new Ui(stream);
            Command command = Parser.parse(input);
            command.execute(tasks, replyUi, storage);
            isExit = command.isExit();
            return stripLeadingIndent(buffer.toString(StandardCharsets.UTF_8));
        } catch (WodanException e) {
            isExit = false;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the last {@code getResponse} call was an exit command.
     *
     * @return {@code true} if the GUI should close after showing the last reply.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts the text UI using {@code data/wodan.txt} relative to the working directory.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new Wodan(DEFAULT_SAVE_PATH).run();
    }

    /**
     * Returns {@code text} with the console indent stripped from each line.
     *
     * @param text Captured console reply.
     * @return Reply suited to a chat bubble.
     */
    private static String stripLeadingIndent(String text) {
        String[] lines = text.split("\\R", -1);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            result.append(lines[i].replaceFirst("^[ ]{4,8}", ""));
            if (i < lines.length - 1) {
                result.append('\n');
            }
        }
        return result.toString().strip();
    }
}
