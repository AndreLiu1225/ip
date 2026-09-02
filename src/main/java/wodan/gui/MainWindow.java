package wodan.gui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import wodan.Wodan;

/**
 * Controller for the main chat window.
 */
public class MainWindow extends VBox {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Wodan wodan;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/wanderer.png"));
    private final Image wodanImage = new Image(
            this.getClass().getResourceAsStream("/images/wodan.png"));

    /**
     * Binds the scroll pane to the dialog list so the latest reply stays in view.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.setStyle("-fx-text-fill: #FFFFFF; -fx-control-inner-background: #1e222c;");
    }

    /**
     * Connects this window to {@code wodan} and shows the greeting.
     *
     * @param wodan Chatbot that produces replies.
     */
    public void setWodan(Wodan wodan) {
        this.wodan = wodan;
        dialogContainer.getChildren().add(
                DialogBox.getWodanDialog(wodan.getGreeting(), wodanImage));
    }

    /**
     * Shows the typed command and Wodan's reply, then clears the input field.
     * Closes the window shortly after a {@code bye} command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String response = wodan.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getWodanDialog(response, wodanImage));
        userInput.clear();

        if (wodan.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> {
                Stage stage = (Stage) userInput.getScene().getWindow();
                stage.close();
            });
            delay.play();
        }
    }
}
