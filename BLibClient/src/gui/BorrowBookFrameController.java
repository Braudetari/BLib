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
import java.time.temporal.ChronoUnit;

import client.ClientUI;
import common.User.UserType;

public class BorrowBookFrameController implements IController {
	@FXML
	private AnchorPane parentNode;
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
 
        
        //frame.initializeBookId(bookId);
        
    }
    
    public void initializeText(String bookId) {
        if(bookId!=null) {
        	bookIdTxt.setText(bookId);
        }
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
        
        int success =  ClientUI.chat.requestServerToBorrowBook(bookId, clientId, borrowDate, returnDate, "id");
        String message= String.format("Subscriber ID: %s\nCould not borrow book\n%s", clientId, ClientUI.chat.getClientLastResponses()[2]);
        if(success>0) {
            message = String.format("Subscriber ID: %s\nBorrow Date: %s\nReturn Date: %s", clientId, borrowDate, returnDate);
        }
        showAlert("Book Borrow Result", message);
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
