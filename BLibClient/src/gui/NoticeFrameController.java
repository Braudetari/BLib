package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for the notice frame UI. Displays a notification message to the user.
 */
public class NoticeFrameController {

    /**
     * The title of the notice frame window.
     */
    private final static String title = "Notice";

    @FXML
    private Button btnOK = null;

    @FXML
    private Label labelmsg;

    /**
     * Sets the text message to be displayed in the notice frame.
     *
     * @param text the message to display in the notice frame.
     */
    public void loadText(String text) {
        labelmsg.setText(text);
    }

    /**
     * Starts the notice frame UI and displays the specified message.
     *
     * @param noticeMessage the message to be displayed in the notice frame.
     * @throws IOException if there is an error loading the FXML file.
     */
    public void start(String noticeMessage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/NoticeFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/styleNotice.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        NoticeFrameController controller = loader.getController();
        controller.loadText(noticeMessage);
    }

    /**
     * Handles the "OK" button click event. Closes the notice frame window.
     *
     * @param event the action event triggered by the button click.
     * @throws Exception if there is an error during the event handling.
     */
    @FXML
    public void getOKBtn(ActionEvent event) throws Exception {
        // Close the window
        Stage thisStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        thisStage.close();
    }

}
