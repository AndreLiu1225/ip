import java.nio.file.Path;

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

        boolean isExit = false;
        while (!isExit) {
            if (!ui.hasCommand()) {
                break;
            }
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (WodanException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts the chatbot using {@code data/wodan.txt} relative to the working directory.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        new Wodan(DEFAULT_SAVE_PATH).run();
    }
}
