package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.User;
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

public class SearchBookFrameController implements IController {

    @FXML
    private ComboBox<String> searchOptions;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnSearch;
    
    @FXML
    private Button btnLend;
    
    @FXML
    private Button btnReserve;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> colBookName;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, String> colDescription;
    
    @FXML
    private TableColumn<Book, String> colStatus;

    @FXML
    private TableColumn<Book, String> colLocation;

    @FXML
    private TableColumn<Book, String> colReturnDate;
    
	private Book selectedBook=null;
	private User.UserType permission;
    private List<Book> booksList = null;
    private ObservableList<Book> booksData = FXCollections.observableArrayList();
    //private User.UserType permission;
    
    public void initializeFrame() {
    	// Add search options
    	initializeButtons();
        searchOptions.getItems().addAll("Search by Name", "Search by Description", "Search by Genre");
        searchOptions.setValue("Search by Name");

        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
		colCategory.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGenre()));
		colLocation.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLocation()));
		colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));
//		colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getClosestReturnDate()));
//		colStatus.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
		colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>("EMPTY"));
		colStatus.setCellValueFactory(cellData -> new SimpleObjectProperty<>("EMPTY"));
		booksTable.setItems(booksData);
        
    }
    private void initializeButtons() {
    	Button[] listOfButtons = {btnLend,btnReserve};
		User.UserType[] buttonPermission = {User.UserType.LIBRARIAN, User.UserType.SUBSCRIBER};
        for(int i=0;i<listOfButtons.length;i++) {
	    	boolean checkPermission = buttonPermission[i].equals(permission);
	    	if(checkPermission) {
				listOfButtons[i].setVisible(true);
	    	}
	    	else {
	    		listOfButtons[i].setVisible(false);
	    	}
		}
	}

    public void setPermission(User.UserType permission) {
    	this.permission = permission;
    }
    
    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Search text cannot be empty.");
            return;
        }
        
        switch(selectedOption){
        	case "Search by Name":
        			booksList = ClientUI.chat.requestServerSearchForBooks("book_name", searchText);
        		break;
        	case "Search by Description":
        		booksList = ClientUI.chat.requestServerSearchForBooks("book_description", searchText);
    		break;
        	case "Search by Genre":
        		booksList = ClientUI.chat.requestServerSearchForBooks("book_genre", searchText);
    		break;
        	default:
        		break;
        }
        //Show only unique values by SerialId if Subscriber
        if(!permission.equals(User.UserType.LIBRARIAN)) {
        	List<Book> booksListUnique = new ArrayList<Book>();
        	for(Book book : booksList) {
        		boolean bookFound = false;
        		for(Book bookUnique : booksListUnique) {
        			if(book.getSerial_id() == bookUnique.getSerial_id()) {
        				bookFound = true;
                		break;
        			}
        		}
        		if(!bookFound)
        			booksListUnique.add(book);
        	}
        	booksList = booksListUnique;
        }
        
        //Get BookInfo For List
        //Here
        
    	booksData.clear();
        if(booksList != null) {
            booksData.addAll(booksList);
        }
    }

	@FXML
	private void SelectRow(MouseEvent event) throws Exception{
		selectedBook = booksTable.getSelectionModel().getSelectedItem();
		if(selectedBook != null) {
			btnReserve.setDisable(false);
			btnLend.setDisable(false);
		}
		else {
			btnReserve.setDisable(true);
			btnLend.setDisable(true);
		}
	}
    
    @FXML
    private void Lend(ActionEvent event) {
    	System.out.println("Lend" + selectedBook);
    	Stage thisStage = ((Stage)((Node)event.getSource()).getScene().getWindow());
    	try {
			(new BorrowBookFrameController()).start(thisStage,"" + selectedBook.getId());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    private void Reserve(ActionEvent event) {
    	System.out.println("Reserve" + selectedBook);
    	
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void start(Stage primaryStage,User.UserType permission) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/SearchBookFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Search Book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
