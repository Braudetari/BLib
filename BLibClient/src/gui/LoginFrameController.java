package gui;

import client.ClientUI;
import common.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the login screen of the application.
 * Handles user login and guest login functionality.
 */
public class LoginFrameController extends Application {

    @FXML
    private Label labelmsg;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnGuest;

    /**
     * Handles the action when the "Login" button is clicked.
     * Validates the username and password fields and attempts to log in the user.
     *
     * @param event the action event triggered by the button click.
     * @throws Exception if an error occurs during the login process.
     */
    @FXML
    private void getLoginBtn(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            (new NoticeFrameController()).start("Username or password cannot be empty.");
            return;
        }

        // Attempt to log in to the server
        Object[] values = ClientUI.chat.LoginToServer(username, password);
        String[] lr = ClientUI.chat.getClientLastResponses();

        if (lr[0].equals("error")) {
            (new NoticeFrameController()).start(lr[2]);
            return;
        } else {
            String fullName = (String) values[1];
            User user = (User) values[0];
            Stage thisStage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
            (new MenuUIController()).start(thisStage, user.getType(), fullName);
        }
    }

    /**
     * Handles the action when the "Enter as Guest" button is clicked.
     * Logs the user in as a guest and transitions to the main menu.
     *
     * @param event the action event triggered by the button click.
     * @throws Exception if an error occurs during the guest login process.
     */
    @FXML
    private void getGuestBtn(ActionEvent event) throws Exception {
        Stage thisStage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
        (new MenuUIController()).start(thisStage, User.UserType.GUEST, "GUEST");
    }

    /**
     * Starts the application by setting up the login screen.
     *
     * @param primaryStage the primary stage of the application.
     * @throws Exception if an error occurs while loading the FXML or setting up the stage.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML and set up the scene
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LoginFrame.fxml"));
        Scene scene = new Scene(root);

        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());

        // Configure the primary stage
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    /**
     * The main method to launch the application.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
