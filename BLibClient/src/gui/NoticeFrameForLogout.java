package gui;

import java.io.IOException;

import client.ClientUI;
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
 * Controller class for the logout confirmation notice frame UI.
 * Displays a confirmation dialog asking the user if they want to log out.
 */
public class NoticeFrameForLogout {

    /**
     * The title of the notice frame window.
     */
    private final static String title = "Notice";

    @FXML
    private Button btnConfirm = null;

    @FXML
    private Button btnCancel = null;

    @FXML
    private Label labelmsg;

    /**
     * The parent stage to return to after logout.
     */
    private Stage parentStage;

    /**
     * Sets the text message to be displayed in the notice frame.
     *
     * @param text the message to display in the notice frame.
     */
    public void loadText(String text) {
        labelmsg.setText(text);
    }

    /**
     * Sets the parent stage of this notice frame.
     *
     * @param parentStage the parent stage to set.
     */
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    /**
     * Starts the logout confirmation notice frame UI.
     *
     * @param parentStage the parent stage to return to after logout.
     * @throws IOException if there is an error loading the FXML file.
     */
    public void start(Stage parentStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/NoticeFrameForLogout.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/NoticeFrameForLogout.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
        NoticeFrameForLogout controller = loader.getController();
        controller.setParentStage(parentStage);
        controller.loadText("Are you sure you want to Logout?");
    }

    /**
     * Handles the "Cancel" button click event. Closes the notice frame without logging out.
     *
     * @param event the action event triggered by the button click.
     * @throws Exception if there is an error during the event handling.
     */
    @FXML
    public void getCancelBtn(ActionEvent event) throws Exception {
        // Close the notice frame window
        Stage thisStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        thisStage.close();
    }

    /**
     * Handles the "Confirm" button click event. Logs the user out and returns to the login screen.
     *
     * @param event the action event triggered by the button click.
     * @throws Exception if there is an error during the event handling or UI transition.
     */
    @FXML
    public void getConfirmBtn(ActionEvent event) throws Exception {
        // Close the notice frame window
        Stage thisStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        thisStage.close();
        // Log out from the server
        ClientUI.chat.LogoutFromServer();
        // Return to the login screen
        (new LoginFrameController()).start(parentStage);
    }
}
