package gui;

import client.ClientUI;
import common.Client;
import common.loginInfo;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginFrameController extends Application {

    @FXML
    private Label labelmsg;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnGuest;

    // Method to handle the "Enter" button action
    @FXML
    private void getOKBtn(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            labelmsg.setText("Username or password cannot be empty.");
            return;
        }

        // Create a loginInfo object
        loginInfo info = new loginInfo(username, password);
        System.out.println(info);

        // Request the client from the server
        Client client1 ;
        if (ClientUI.chat == null) {
            System.err.println("Error: ClientUI.chat is not initialized!");
            // Handle the error appropriately, such as throwing an exception or returning a default value
            return; // Or return null if the method has a return type
        }
        
        client1 = ClientUI.chat.requestClientFromServer(info);

        // Check if the client is valid
        if (client1 != null) {
        	 MenuUIController MenuUIController1 = new MenuUIController();
        	 MenuUIController1.start(new Stage(),client1);
        } else {
        	String noticeMessage = "invalid username or password";
			 NoticeFrameController noticeFrameController = new NoticeFrameController();
			 noticeFrameController.start(noticeMessage);
        }
    }


    // Method to handle the "Enter as Guest" button action
    @FXML
    private void getGuestBtn(ActionEvent event) {
        labelmsg.setText("Logged in as guest.");
        // Add logic to proceed as a guest (e.g., load a guest view)
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML and set up the scene
        Parent root = FXMLLoader.load(getClass().getResource("loginFrame.fxml"));
        Scene scene = new Scene(root);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("LoginFrame.css").toExternalForm());

        // Configure the primary stage
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
