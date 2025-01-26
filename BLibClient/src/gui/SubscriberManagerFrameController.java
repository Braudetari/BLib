package gui;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.DateUtil;
import common.Subscriber;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class SubscriberManagerFrameController implements IController{
	
	@FXML
	private Button btnOpen = null;
	@FXML
	private Label lblText = null;
	@FXML
	private Button btnSearch=null;
	@FXML
    private ComboBox<String> searchOptions;
	@FXML
	private TableView<Subscriber> tblSubscribers = null;
	@FXML
	private TableColumn<Subscriber, Integer> tblColumnId = null;
	@FXML
	private TableColumn<Subscriber, String> tblColumnName = null;
	@FXML
	private TableColumn<Subscriber, String> tblColumnStatus = null;
	@FXML
	private TextField searchField=null;
	
	private MenuUIController mainController;
	private final ObservableList<Subscriber> observableSubscribers = FXCollections.observableArrayList();
	private List<Subscriber> subscriberList;
	private Subscriber selectedSubscriber=null;
	private static boolean flagKillInfoListenThread = false;
	
	@FXML
	private void initializeTable() {
		
        searchOptions.getItems().addAll("Search by Name", "Search by Subscriber ID", "Search by Frozen Subscriber");
        searchOptions.setValue("Search by Name");

		if(subscriberList != null) {
			observableSubscribers.addAll(subscriberList);
		}
		
		tblColumnId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberId()));
		tblColumnName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberName()));
		tblColumnStatus.setCellValueFactory(cellData -> {
			String bool = (cellData.getValue().isFrozen()) ? "Frozen" : "Active";
			return new SimpleObjectProperty<>(bool);
		});
        tblSubscribers.setItems(observableSubscribers);
	}
	
	public void start(Stage primaryStage) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/gui/SubscriberManagerFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setTitle("Subscriber Management");
		primaryStage.setScene(scene);		
		primaryStage.show();
		SubscriberManagerFrameController controller = loader.getController();
		controller.initializeTable();
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
        			subscriberList = ClientUI.chat.requestServer;
        		break;
        	case "Search by Subscriber ID":
        		subscriberList  = ClientUI.chat.requestServerSearchForBooks("book_description", searchText);
    		break;
        	case "Search by Frozen Subscriber":
        		subscriberList  = ClientUI.chat.requestServerSearchForBooks("book_genre", searchText);
    		break;
        	default:
        		break;
        }
        
//        //Get BookInfo For List
//        Object[] bookInfoList = ClientUI.chat.requestServerForBookListAvailibilityInfo(booksList);
//        if(bookInfoList == null) {
//        	showAlert(AlertType.ERROR, "Error", "Could not get book search info");
//        	return;
//        }
//        
//        try {
//	        @SuppressWarnings("unchecked")
//			List<Boolean> booleanList = (List<Boolean>)bookInfoList[0];
//	        @SuppressWarnings("unchecked")
//			List<LocalDate> dateList = (List<LocalDate>)bookInfoList[1];
//	        //must be same size
//	        booksAvailibility.clear();
//	        booksClosestReturnDate.clear();
//	        for(int i=0;i<booleanList.size();i++) {
//	        	if(booleanList.get(i) == true)
//	        		booksAvailibility.add("Available");
//	        	else
//	        		booksAvailibility.add("Unavailable");
//	        	if(dateList.get(i) != null)
//	        		booksClosestReturnDate.add(DateUtil.DateToString(dateList.get(i)));
//	        	else
//	        		booksClosestReturnDate.add("");
//	        }
//        }
//        catch(Exception e) {
//        	System.err.println("Could not show book availibility info");
//        }
//    	booksData.clear();
//        if(booksList != null) {
//            booksData.addAll(booksList);
//        }
    }
	
	private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
	
	
	@FXML
	private void Open(ActionEvent event) throws Exception{
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
	private void SelectRow(MouseEvent event) throws Exception{
		selectedSubscriber = tblSubscribers.getSelectionModel().getSelectedItem();
		if(selectedSubscriber != null) {
			btnOpen.setDisable(false);
		}
		else {
			btnOpen.setDisable(true);
		}
	}
	
	@FXML
	private void getStudent(ActionEvent event) throws Exception {
//	    SubscriberSearchFrameController subscriberManagerFrame = new SubscriberSearchFrameController();
//	    subscriberManagerFrame.start(new Stage());
	    
	}

	
	
}
