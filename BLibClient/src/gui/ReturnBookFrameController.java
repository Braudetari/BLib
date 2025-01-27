package gui;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ReturnBookFrameController {

    @FXML
    private TextField bookIdTxt;

    @FXML
    private Button returnBtn;

    @FXML
    private void onReturnBtnClick(ActionEvent event) {
        String bookId = bookIdTxt.getText().trim();

        if (bookId.isEmpty()) {
            showAlert("Error", "Book ID cannot be empty.");
            return;
        }

        // Simulate the book return process (you can replace this with actual logic, such as updating a database)
        if (returnBook(bookId)) {
            showAlert("Success", "Book ID " + bookId + " has been successfully returned.");
        } else {
            showAlert("Error", "Could not find the book with ID " + bookId + ". Please try again.");
        }
    }

    private boolean returnBook(String bookId) {
	        ClientUI.chat.requestServerToReturnBook(bookId);
	        String[] lr = ClientUI.chat.getClientLastResponses();
	        if(lr[0] != null && lr[0].contentEquals("error")) {
	        	return false;
	        }
	        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
