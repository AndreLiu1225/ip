package wodan.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

/**
 * A chat row with a speaker portrait and a text bubble.
 */
public class DialogBox extends HBox {
    private static final double PORTRAIT_SIZE = 44;

    @FXML
    private Text dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load a dialog box.", e);
        }

        dialog.setText(text);
        dialog.setFill(Color.WHITE);
        dialog.setFontSmoothingType(FontSmoothingType.GRAY);
        dialog.setStyle("-fx-fill: #FFFFFF;");
        displayPicture.setImage(img);
        Circle clip = new Circle(PORTRAIT_SIZE / 2, PORTRAIT_SIZE / 2, PORTRAIT_SIZE / 2);
        displayPicture.setClip(clip);
    }

    /**
     * Flips the row so the portrait is on the left and the bubble on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a right-aligned dialog for the wanderer's command.
     *
     * @param text Command typed by the user.
     * @param img Wanderer portrait.
     * @return Dialog row aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Returns a left-aligned dialog for Wodan's reply.
     *
     * @param text Reply from Wodan.
     * @param img Wodan portrait.
     * @return Dialog row aligned to the left.
     */
    public static DialogBox getWodanDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.flip();
        dialogBox.getStyleClass().add("wodan-dialog");
        return dialogBox;
    }
}
