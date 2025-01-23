package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterClientFrameController {

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

        // Simulate client registration here (e.g., saving to database)
        String message = String.format("Registration Successful!\n\nName: %s %s\nUsername: %s\nEmail: %s", firstName, lastName, username, email);
        showAlert("Success", message);
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
}
