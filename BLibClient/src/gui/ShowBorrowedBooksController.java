package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.BorrowedBook;
import common.User;
import common.User.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ShowBorrowedBooksController implements IController{

    @FXML
    private Button btnExtend;

    @FXML
    private TableView<BorrowedBook> borrowedBooksTable;

    @FXML
    private TableColumn<BorrowedBook, String> colBookName;

    @FXML
    private TableColumn<BorrowedBook, String> colBorrowedDate;

    @FXML
    private TableColumn<BorrowedBook, String> colReturnDate;

    private BorrowedBook selectedBook = null;

    private List<BorrowedBook> borrowedBooksList = null;
    private List<Boolean> extendableList = null;
    private ObservableList<BorrowedBook> borrowedBooksData = FXCollections.observableArrayList();
    
    public void initializeFrame() {
        // Initialize the button and hide it initially
        btnExtend.setVisible(true);
        btnExtend.setDisable(true);

        // Set up table columns
        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowedBook().getName()));
        colBorrowedDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowedDate().toString()));
        colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturnDate().toString()));
        
        borrowedBooksTable.setItems(borrowedBooksData);

        // Fetch and display all borrowed books for the client
        
        RefreshBorrowedBooks();
    }

    @FXML
    private void SelectRow(MouseEvent event) throws Exception {
        selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Show the "Extend" button when a row is selected
        	int index = borrowedBooksList.indexOf(selectedBook);
        	if(extendableList.get(index)){
                btnExtend.setDisable(false);
        	}
        	else {
                btnExtend.setDisable(true);
        	}
        } else {
            // Hide the "Extend" button when no row is selected
        	btnExtend.setDisable(true);
        }
    }
    
    @FXML
    private void Extend(ActionEvent event) {
        System.out.println("Extend the loan for: " + selectedBook);

        // Logic to extend the loan (You can implement the actual extension logic here)
        // You can send a request to the server or update the database accordingly
        int success = ClientUI.chat.requestServerToExtendBookReturnDate(selectedBook.getBorrowedBook().getId(), 14);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Extend Loan");
        if(success>0) {
            // For now, let's just show an alert
            alert.setHeaderText("Loan Extended Successfully");
            alert.setContentText("The loan for '" + selectedBook.getBorrowedBook().getName() + "' has been extended.");      
        }
        else {
        	alert.setHeaderText("Failed to Extend Loan");
            alert.setContentText("The loan for '" + selectedBook.getBorrowedBook().getName() + "' could not be extended.");
        }
        alert.showAndWait();
        RefreshBorrowedBooks();
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/ShowBorrowedBooksFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Show Borrowed Books");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	private void RefreshBorrowedBooks() {
		borrowedBooksData.clear();
        
        User user = ClientUI.chat.getClientUser();
        borrowedBooksList = ClientUI.chat.requestServerForBorrowedBooksBySubscriber(user.getId());
        
        List<Integer> intList = new ArrayList<Integer>();
        for(BorrowedBook bb : borrowedBooksList) {
        	intList.add(bb.getBorrowedBook().getId());
        }
        extendableList = ClientUI.chat.requestServerForBookListExtendability(intList);
        
        if (borrowedBooksList != null) {
            borrowedBooksData.addAll(borrowedBooksList);
        }
	}
	
	@Override
	public void setMainController(MenuUIController controller) {
		//
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}

	
}
