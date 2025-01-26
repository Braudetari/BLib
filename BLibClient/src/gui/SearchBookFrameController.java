package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.DateUtil;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchBookFrameController implements IController {
	@FXML
	private AnchorPane parentNode = null;
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
    
    private MenuUIController mainController;
	private Book selectedBook=null;
	private User.UserType permission;
    private List<Book> booksList = null;
    private List<String> booksAvailibility = null;
    private List<String> booksClosestReturnDate = null;
    private ObservableList<Book> booksData = FXCollections.observableArrayList();
    //private User.UserType permission;
    
    public void initializeFrame() {
    	// Add search options
    	initializeButtons();
        searchOptions.getItems().addAll("Search by Name", "Search by Description", "Search by Genre");
        searchOptions.setValue("Search by Name");

        booksAvailibility = new ArrayList<String>();
        booksClosestReturnDate = new ArrayList<String>();
        
        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
		colCategory.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGenre()));
		colLocation.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLocation()));
		colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));
		colStatus.setCellValueFactory(cellData -> {
			Book book = cellData.getValue();
			int row = booksData.indexOf(book);
			if(row>=0){
				String availibility = booksAvailibility.get(row);
				return new SimpleObjectProperty(availibility);
			}
			else {
				return new SimpleObjectProperty("");	
			}
			
		});
		colReturnDate.setCellValueFactory(cellData -> {
			Book book = cellData.getValue();
			int row = booksData.indexOf(book);
			if(row>=0){
				String closestReturnDate = booksClosestReturnDate.get(row);
				return new SimpleObjectProperty(closestReturnDate);
			}
			else {
				return new SimpleObjectProperty("");	
			}
			
		});
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
        Object[] bookInfoList = ClientUI.chat.requestServerForBookListAvailibilityInfo(booksList);
        if(bookInfoList == null) {
        	showAlert(AlertType.ERROR, "Error", "Could not get book search info");
        	return;
        }
        
        try {
	        @SuppressWarnings("unchecked")
			List<Boolean> booleanList = (List<Boolean>)bookInfoList[0];
	        @SuppressWarnings("unchecked")
			List<LocalDate> dateList = (List<LocalDate>)bookInfoList[1];
	        //must be same size
	        booksAvailibility.clear();
	        booksClosestReturnDate.clear();
	        for(int i=0;i<booleanList.size();i++) {
	        	if(booleanList.get(i) == true)
	        		booksAvailibility.add("Available");
	        	else
	        		booksAvailibility.add("Unavailable");
	        	if(dateList.get(i) != null)
	        		booksClosestReturnDate.add(DateUtil.DateToString(dateList.get(i)));
	        	else
	        		booksClosestReturnDate.add("");
	        }
        }
        catch(Exception e) {
        	System.err.println("Could not show book availibility info");
        }
    	booksData.clear();
        if(booksList != null) {
            booksData.addAll(booksList);
        }
    }

    @FXML
    private void SelectRow(MouseEvent event) throws Exception{
        selectedBook = booksTable.getSelectionModel().getSelectedItem();
        //if a book is selected and available
        if(selectedBook != null){
            if(booksAvailibility.get(booksData.indexOf(selectedBook)).contentEquals("Available")) {
                btnReserve.setDisable(true);
                btnLend.setDisable(false);
            }
            else {
                btnReserve.setDisable(false);
                btnLend.setDisable(true);
            }
        }
        else {
            btnReserve.setDisable(true);
            btnLend.setDisable(true);
        }
    }
    
    @FXML
    private void Lend(ActionEvent event) {
    	System.out.println("Lend" + selectedBook);
    	try {
    		IController genericController = mainController.loadFXMLIntoPane("/gui/BorrowBookFrame.fxml");
    		if(genericController instanceof BorrowBookFrameController) {
    			BorrowBookFrameController borrowController = (BorrowBookFrameController)genericController;
    			borrowController.initializeText("" + selectedBook.getId());
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void setMainController(MenuUIController controller) {
    	this.mainController = controller;
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
