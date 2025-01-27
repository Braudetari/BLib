package gui;

import client.ClientUI;
import common.User.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterSubscriberFrameController implements IController{
	private MenuUIController mainController;
	
    @FXML
    private TextField firstNameTxt;

    @FXML
    private TextField lastNameTxt;

    @FXML
    private TextField phoneTxt;

    @FXML
    private TextField usernameTxt;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private TextField emailTxt;

    @FXML
    private Button registerBtn;

    @FXML
    private void onRegisterBtnClick(ActionEvent event) {
        String firstName = firstNameTxt.getText().trim();
        String lastName = lastNameTxt.getText().trim();
        String phoneNumber = phoneTxt.getText().trim();
        String username = usernameTxt.getText().trim();
        String password = passwordTxt.getText().trim();
        String email = emailTxt.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert("Error", "Please enter a valid phone number.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }

        boolean success = ClientUI.chat.requestServerToRegisterSubscriber(username, password, firstName + " " + lastName, email, phoneNumber);
        if(!success) {
        	showAlert("Error", "Subscriber registration failed.");
        	return;
        }
        else {
        	
        	showAlert("Notice", "Subscriber registered successfully.");
            try {
            	mainController.loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml");
            }
            catch(Exception e) {
            	e.printStackTrace();
            	System.err.println("Could go back to  Subscriber Manager Screen");
            }
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Simple validation for phone number (you can adjust this regex as per your requirement)
        return phoneNumber.matches("^[0-9]{10}$");
    }

    private boolean isValidEmail(String email) {
        // Simple email validation (basic)
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

	@Override
	public void initializeFrame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMainController(MenuUIController controller) {
		this.mainController = controller;
	}

	@Override
	public void initializeFrame(Object object) {
		// TODO Auto-generated method stub
		
	}
}
