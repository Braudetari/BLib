package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import client.ClientUI;

public class BorrowBookFrameController {

    @FXML
    private TextField subscriberIdTxt;
  
    @FXML
    private TextField bookIdTxt;

    @FXML
    private DatePicker borrowDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    @FXML
    private Button borrowBtn;
    
    //private int bookId;

    public void start(Stage primaryStage, String bookId) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowBookFrame.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowBookFrame.css").toExternalForm());
        primaryStage.setTitle("Borrow Book");
        primaryStage.setScene(scene);
        primaryStage.show();
        if(bookId!=null) {
        	bookIdTxt.setText(bookId);
        }
        
        //frame.initializeBookId(bookId);
        
    }
    
    
//    private void setBookId(String bookId) {
//    	if(bookId!=null) {
//    		this.bookId=bookId;
//    	}
//    }

    @FXML
    private void initialize() {
    	
        // Set today's date as the default borrow date
        borrowDatePicker.setValue(LocalDate.now());
        // Set the max return date to 2 weeks from today
        returnDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now().plusWeeks(2))) {
                    setStyle("-fx-background-color: #ff0000;");
                    setDisable(true);
                } else {
                    setStyle("");
                    setDisable(false);
                }
            }
        });
    }

    @FXML
    private void onBorrowBtnClick(ActionEvent event) {
        String clientId = subscriberIdTxt.getText().trim();
        String bookId = bookIdTxt.getText();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (clientId.isEmpty()) {
            showAlert("Error", "Please enter a valid Client ID.");
            return;
        }
        
        if(bookId.isEmpty()) {
        	showAlert("Error", "Please enter a valid Book ID.");
        }

        if (returnDate == null || returnDate.isBefore(borrowDate) || returnDate.isAfter(borrowDate.plusWeeks(2))) {
            showAlert("Error", "Return date cannot be more than two weeks from the borrow date.");
            return;
        }
        
        ClientUI.chat.requestServerToBorrowBook(bookId, clientId, borrowDate, returnDate, "id");
        // Simulate book borrowing process here (e.g., saving to database)
        String message = String.format("Client ID: %s\nBorrow Date: %s\nReturn Date: %s", clientId, borrowDate, returnDate);
        showAlert("Book Borrowed", message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
