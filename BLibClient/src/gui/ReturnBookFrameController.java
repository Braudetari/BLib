package gui;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller class for handling the return book process in the UI.
 * Provides functionality for entering a book ID and returning the book.
 */
public class ReturnBookFrameController {

    /**
     * TextField for entering the book ID to be returned.
     */
    @FXML
    private TextField bookIdTxt;

    /**
     * Button to initiate the return book process.
     */
    @FXML
    private Button returnBtn;

    /**
     * Handles the action event when the return button is clicked.
     * Validates the input and attempts to return the book.
     * 
     * @param event The action event triggered by the return button.
     */
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

    /**
     * Sends a request to the server to return the specified book.
     * 
     * @param bookId The ID of the book to be returned.
     * @return true if the book return was successful; false otherwise.
     */
    private boolean returnBook(String bookId) {
        ClientUI.chat.requestServerToReturnBook(bookId);
        String[] lr = ClientUI.chat.getClientLastResponses();
        if (lr[0] != null && lr[0].contentEquals("error")) {
            return false;
        }
        return true;
    }

    /**
     * Displays an alert dialog with the given title and message.
     * 
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
