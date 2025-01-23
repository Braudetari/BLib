package gui;

import client.ChatClient;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginFrameController extends Application {

    @FXML
    private Label labelmsg;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnGuest;

    // Method to handle the "Enter" button action
    @FXML
    private void getLoginBtn(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        //start menuUI
        //User user = ClientUI.chat.LoginToServer(username, password)
        
     // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            (new NoticeFrameController()).start("Username or password cannot be empty.");
            return;
        }
        
    	Object[] values= ClientUI.chat.LoginToServer(username, password);
    	String[] lr =ClientUI.chat.getClientLastResponses();
    	if(lr[0].equals("error")) {
            return;
    	}
    	else {
    		String fullName= (String) values[1];
    		User user= (User) values[0];
    		(new MenuUIController()).start(user.getType(), fullName);
    		((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close Frame
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
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LoginFrame.fxml"));
        Scene scene = new Scene(root);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());

        // Configure the primary stage
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}