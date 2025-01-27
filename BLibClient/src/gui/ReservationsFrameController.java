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

public class ReservationsFrameController implements IController{

    @FXML
    private Button btnExtend;

    @FXML
    private TableView<Book> reservedTable;

    @FXML
    private TableColumn<Book, String> colBookName;

    @FXML
    private TableColumn<Book, Integer> colBookId;

    @FXML
    private TableColumn<Book, String> colStatus;

    private List<Book> reservedList = null;
    private List<String> booksAvailibilityString = null;
    private List<Boolean> booksAvailibility = null;
    private ObservableList<Book> reservedData;
    
    public void initializeFrame() {
    	reservedData = FXCollections.observableArrayList();
        colBookId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
		colStatus.setCellValueFactory(cellData -> {
			Book book = cellData.getValue();
			int row = reservedData.indexOf(book);
			if(row>=0){
				boolean available = booksAvailibility.get(row);
				return new SimpleObjectProperty((available)? "Available" : "Reserved");
			}
			else {
				return new SimpleObjectProperty("");	
			}
			
		});
		
		reservedTable.setItems(reservedData);
		
        reservedList = ClientUI.chat.requestServerForReservedBooks(ClientUI.chat.getClientUser().getId());
        if(reservedList == null) {
        	reservedList = new ArrayList<Book>();
        }
        Object[] bookInfoList = ClientUI.chat.requestServerForBookListAvailibilityInfo(reservedList);
        try {
            booksAvailibility = (List<Boolean>)bookInfoList[0];
        }
        catch(Exception e) {
        	booksAvailibility = new ArrayList<Boolean>();
        }
        
        reservedData.clear();
        if (reservedList != null) {
        	reservedData.addAll(reservedList);
        }
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
	public void initializeFrame(Object object) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setMainController(MenuUIController controller) {
		// TODO Auto-generated method stub
		
	}

	
}
