package gui;

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
        // Placeholder for actual logic to handle the book return (e.g., update the database or inventory)
        // Here, we simulate a successful return for any valid book ID.
        return true;  // In a real system, you'd check if the book ID exists and update the status.
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
