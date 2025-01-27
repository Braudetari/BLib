package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.DateUtil;
import common.DetailedHistory;
import common.Subscriber;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HistoryFrameController implements IController {
	@FXML
	private AnchorPane parentNode = null;
    @FXML
    private ComboBox<String> searchOptions;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnBack;
    
    @FXML
    private Button btnAddNote;
    
    @FXML
    private TableView<DetailedHistory> historyTable;

    @FXML
    private TableColumn<DetailedHistory, String> colAction;

    @FXML
    private TableColumn<DetailedHistory, String> colDate;

    @FXML
    private TableColumn<DetailedHistory, String> colDescription;
    
    private MenuUIController mainController;
	private DetailedHistory selectedLine=null;
	private User.UserType permission;
	private Subscriber importedSubscriber;
    private List<DetailedHistory> historyList = null;
    //private List<String> booksAvailibility = null;
    //private List<String> booksClosestReturnDate = null;
    private ObservableList<DetailedHistory> historyData;
    //private User.UserType permission;
    private Integer userHistoryId = 0;
    
    public void setOjbect(Object object) {
    	if(object instanceof Integer)
    		this.userHistoryId = (Integer)object;
    }
    
    public void initializeHistory() {
    	if(ClientUI.chat.getClientUser().getType().equals(User.UserType.LIBRARIAN)) {
        	btnAddNote.setVisible(true);
    	}
    	else {
    		btnAddNote.setVisible(false);
    	}
    	
    	// Add search options
        searchOptions.getItems().addAll("Action", "Description", "Note");
        searchOptions.setValue("Action");
        
        historyData = FXCollections.observableArrayList();
        colAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAction().toString()));
        colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));
		colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate().toString()));
		historyTable.setItems(historyData);
        
    }
    
    public void refreshHistory() {
    }
    
    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();
        if(importedSubscriber == null && ClientUI.chat.getClientUser().equals(User.UserType.LIBRARIAN)) {
        	//Implement Librarian Request History List
        	historyList = null;
        }
        else {
    		historyList = ClientUI.chat.requestServerForHistoryList(importedSubscriber.getDetailedSubscriptionHistory());
        }
		if(historyList == null) {
			historyList = new ArrayList<DetailedHistory>();
		}
        
        if (searchText.isEmpty()) {
        	searchText = "";
        }
        List<DetailedHistory> filteredHistoryList = new ArrayList<DetailedHistory>();
        for(int i=historyList.size()-1; i>=0 ; i--) {
        	boolean addEntry = false;
        	switch(selectedOption){
        	case "Action":
        			if(historyList.get(i).getAction().toString().toLowerCase().contains(searchText.toLowerCase()))
        					addEntry = true;
        		break;
        	case "Description":
        		if(historyList.get(i).getDescription().toString().toLowerCase().contains(searchText.toLowerCase()))
    					addEntry = true;
    		break;
        	case "Note":
        		if(historyList.get(i).getNote().toString().toLowerCase().contains(searchText.toLowerCase()))
    					addEntry = true;
    		break;
        	default:
        		break;
        	}
        	
        	if(addEntry) {
        		filteredHistoryList.add(historyList.get(i));
        	}
        }
        
        historyData.clear();
        historyData.addAll(filteredHistoryList);
    }

    @FXML
    private void SelectRow(MouseEvent event) throws Exception{
        selectedLine = historyTable.getSelectionModel().getSelectedItem();
        if(selectedLine != null) {
        	btnAddNote.setDisable(false);
        }
        else {
        	btnAddNote.setDisable(true);
        }
    }
    
    @FXML
    private void OpenAddNote(ActionEvent event) {
    	IController genericController = mainController.loadFXMLIntoPane("/gui/AddNoteFrame.fxml");
    	if(genericController instanceof AddNoteFrameController) {
    		AddNoteFrameController addNote = (AddNoteFrameController)genericController;
    		addNote.setObject(new Object[] {selectedLine, importedSubscriber, userHistoryId});
    		
    	}
    }
    
    @FXML
	private void handleBackBtn(ActionEvent event) throws Exception{
		try {
	    		IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
	    		if(genericController instanceof SubscriberInfoFrameController) {
	    			SubscriberInfoFrameController infoController = (SubscriberInfoFrameController)genericController;
	    			infoController.setObject(importedSubscriber);
	    			infoController.initializeSubscriberInfo();
	    		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public void setMainController(MenuUIController controller) {
    	this.mainController = controller;
    }
    
    
    public void start(Stage primaryStage,User.UserType permission) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/HistoryFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

@Override
public void setPermission(UserType type) {
	// TODO Auto-generated method stub
	permission=type;
}

@Override
public void setObject(Object object) {
	userHistoryId = (Integer)((Object[])object)[1];
	importedSubscriber = (Subscriber)((Object[])object)[0];
}

@Override
public void initializeFrame(Object object) {
	// TODO Auto-generated method stub
	
}

	@Override
	public void initializeFrame() {
		mainController.setPaneTitle("Detailed History");
		
	}
}
