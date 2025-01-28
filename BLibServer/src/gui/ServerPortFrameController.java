package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.BLibServer;
import server.ServerUI;

/**
 * Controller class for managing the Server Port UI.
 * Provides functionality to set up the server port and transition to the server connections view.
 */
public class ServerPortFrameController {

    /**
     * Instance of the BLibServer to manage server operations.
     */
    private BLibServer server;

    /**
     * Temporary variable for additional processing.
     */
    String temp = "";

    /**
     * Title of the Server Port window.
     */
    private final static String title = "Server - Port";

    /**
     * Button to exit the application.
     */
    @FXML
    private Button btnExit = null;

    /**
     * Button to confirm the entered port and start the server.
     */
    @FXML
    private Button btnDone = null;

    /**
     * Label for displaying messages related to the port.
     */
    @FXML
    private Label lbllist;

    /**
     * TextField for entering the server port.
     */
    @FXML
    private TextField portxt;

    /**
     * ObservableList for managing any dropdown or list UI elements (currently unused).
     */
    ObservableList<String> list;

    /**
     * Retrieves the entered port from the TextField.
     * 
     * @return The port entered in the TextField as a String.
     */
    private String getport() {
        return portxt.getText();
    }

    /**
     * Handles the Done button action to validate the entered port and start the server.
     * Transitions to the server connections view if successful.
     * 
     * @param event The ActionEvent triggered by the Done button.
     * @throws Exception If there is an error during server startup or UI transition.
     */
    public void Done(ActionEvent event) throws Exception {
        String p = getport();

        if (p.trim().isEmpty()) {
            System.out.println("You must enter a port number");
        } else {
            Stage thisStage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
            server = ServerUI.runServer(p);
            ServerConnectionsFrameController serverConnectionsFrameController = new ServerConnectionsFrameController();
            serverConnectionsFrameController.start(thisStage, server);
        }
    }

    /**
     * Starts the Server Port UI.
     * 
     * @param primaryStage The primary stage for the application.
     * @throws Exception If there is an error loading the UI.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the Exit button action to terminate the application.
     * 
     * @param event The ActionEvent triggered by the Exit button.
     * @throws Exception If there is an error during the exit process.
     */
    public void getExitBtn(ActionEvent event) throws Exception {
        System.out.println("Exit - Server Port Frame");
        System.exit(0);
    }
}
