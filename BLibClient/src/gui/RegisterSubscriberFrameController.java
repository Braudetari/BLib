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

/**
 * Controller class for the Register Subscriber Frame UI.
 * Handles subscriber registration form input, validation, and submission.
 */
public class RegisterSubscriberFrameController implements IController {
    
    /**
     * Reference to the main menu controller.
     */
    private MenuUIController mainController;

    /**
     * TextField for entering the subscriber's first name.
     */
    @FXML
    private TextField firstNameTxt;

    /**
     * TextField for entering the subscriber's last name.
     */
    @FXML
    private TextField lastNameTxt;

    /**
     * TextField for entering the subscriber's phone number.
     */
    @FXML
    private TextField phoneTxt;

    /**
     * TextField for entering the subscriber's username.
     */
    @FXML
    private TextField usernameTxt;

    /**
     * PasswordField for entering the subscriber's password.
     */
    @FXML
    private PasswordField passwordTxt;

    /**
     * TextField for entering the subscriber's email address.
     */
    @FXML
    private TextField emailTxt;

    /**
     * Button to trigger the subscriber registration process.
     */
    @FXML
    private Button registerBtn;

    /**
     * Handles the Register button click event.
     * Validates the input fields and submits the subscriber's details to the server.
     * 
     * @param event The action event triggered by clicking the Register button.
     */
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
        if (!success) {
            showAlert("Error", "Subscriber registration failed.");
            return;
        } else {
            showAlert("Notice", "Subscriber registered successfully.");
            try {
                mainController.loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml");
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Could not go back to Subscriber Manager Screen");
            }
        }
    }

    /**
     * Validates the format of the phone number.
     * 
     * @param phoneNumber The phone number to validate.
     * @return True if the phone number is valid, false otherwise.
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10}$");
    }

    /**
     * Validates the format of the email address.
     * 
     * @param email The email address to validate.
     * @return True if the email address is valid, false otherwise.
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Displays an alert message to the user.
     * 
     * @param title   The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initializes the frame.
     */
    @Override
    public void initializeFrame() {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the permission level for the current user.
     * 
     * @param type The user type that defines the permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets an object in the controller.
     * 
     * @param object The object to set.
     */
    @Override
    public void setObject(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller to set.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        this.mainController = controller;
    }

    /**
     * Initializes the frame with the given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
        // TODO Auto-generated method stub
    }
}