package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginUiFrameController {

    @FXML
    private TextField UserName;

    @FXML
    private TextField Password;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnGuest;

    @FXML
    private void LoginAsUser(ActionEvent event) {
        String username = UserName.getText().trim();
        String password = Password.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both username and password fields.");
            return;
        }
        
        //Leon Add Login here
        
    }


    @FXML
    private void LoginAsGuest(ActionEvent event) {
        System.out.println("Entering as Guest.");
        
        
        // Add guest logic here
        
        
    }

    
    
    // Method to close the application (if needed)
    private void closeStage() {
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.close();
    }
}

