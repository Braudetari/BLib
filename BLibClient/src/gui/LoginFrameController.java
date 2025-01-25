package gui;

import java.io.IOException;

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
    		Stage thisStage = ((Stage)((Node)event.getSource()).getScene().getWindow());
    		(new MenuUIController()).start(thisStage, user.getType(), fullName);
    	}

    }


    // Method to handle the "Enter as Guest" button action
    @FXML
    private void getGuestBtn(ActionEvent event) throws Exception{
    	(new GuestController()).start();
    	
//    	Object[] values= ClientUI.chat.LoginToServer("GUEST", "GUEST");
//    	String[] lr =ClientUI.chat.getClientLastResponses();
//    	if(lr[0].equals("error")) {
//            return;
//    	}
//    	else {
//    		String fullName= (String) values[1];
//    		User user= (User) values[0];
//    		try {
//        		(new MenuUIController()).start(user.getType(), fullName);
//        		((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close Frame
//    		}
//    		catch(IOException e) {
//    			e.printStackTrace();
//    			System.err.println("Could not start MenuUIController in getGuestBtn");
//    			System.exit(-1);
//    		}
//    	}
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
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}