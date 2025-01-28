package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;
import client.ClientUI;
import common.User.UserType;

/**
 * Controller for the "Borrow Book" frame, allowing subscribers to borrow books.
 * The frame validates input data such as subscriber ID, book ID, borrow date, and return date.
 */
public class BorrowBookFrameController implements IController {
	
	/** The parent node for the frame layout. */
	@FXML
	private AnchorPane parentNode;
	/** Text field for entering the subscriber ID. */
	@FXML
    private TextField subscriberIdTxt;
	/** Text field for entering the book ID. */
    @FXML
    private TextField bookIdTxt;
    /** Date picker for selecting the borrow date. */
    @FXML
    private DatePicker borrowDatePicker;
    /** Date picker for selecting the return date. */
    @FXML
    private DatePicker returnDatePicker;
    /** Button to confirm and process the borrow request. */
    @FXML
    private Button borrowBtn;
    
    
    /**
     * Starts the "Borrow Book" frame.
     * 
     * @param primaryStage the primary stage to display the frame.
     * @param bookId the ID of the book to prefill in the book ID field, if available.
     * @throws Exception if there is an error loading the FXML file.
     */
    public void start(Stage primaryStage, String bookId) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BorrowBookFrame.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowBookFrame.css").toExternalForm());
        primaryStage.setTitle("Borrow Book");
        primaryStage.setScene(scene);
        primaryStage.show();    
    }
    
    /**
     * Initializes the book ID field with a given value.
     * 
     * @param bookId the ID of the book to set in the book ID field.
     */
    public void initializeText(String bookId) {
        if(bookId!=null) {
        	bookIdTxt.setText(bookId);
        }
    }

    /**
     * Initializes the frame. Sets the default borrow date to today and configures the return date picker
     * to allow dates only up to two weeks after the borrow date.
     */
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

    /**
     * Handles the borrow button click event. Validates the input fields and sends a borrow request to the server.
     * Displays an alert with the result of the borrow operation.
     * 
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void onBorrowBtnClick(ActionEvent event) {
        String clientId = subscriberIdTxt.getText().trim();
        String bookId = bookIdTxt.getText();
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (clientId.isEmpty()) {
            showAlert("Error", "Please enter a valid Subscriber ID.");
            return;
        }
        
        if(bookId.isEmpty()) {
        	showAlert("Error", "Please enter a valid Book ID.");
        }

        if (returnDate == null || returnDate.isBefore(borrowDate) || returnDate.isAfter(borrowDate.plusWeeks(2))) {
            showAlert("Error", String.format("Please enter valid dates\nReturn date up to 2 weeks after borrow date."));
            return;
        }
        
        int success =  ClientUI.chat.requestServerToBorrowBook(bookId, clientId, borrowDate, returnDate);
        String message= String.format("Subscriber ID: %s\nCould not borrow book\n%s", clientId, ClientUI.chat.getClientLastResponses()[2]);
        if(success>0) {
            message = String.format("Subscriber ID: %s\nBorrow Date: %s\nReturn Date: %s", clientId, borrowDate, returnDate);
        }
        showAlert("Book Borrow Result", message);
    }
    
    /**
     * Displays an alert dialog with the given title and message.
     * 
     * @param title the title of the alert dialog.
     * @param message the message content of the alert dialog.
     */
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
	public void setMainController(MenuUIController controller) {
		//TODO Aut-generated method stub
		
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeFrame(Object object) {
		// TODO Auto-generated method stub
		
	}
}
