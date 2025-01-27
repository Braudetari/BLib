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

public class SubscriberManagerFrameController implements IController{
	
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
            searchText = "";
        }
        
        switch(selectedOption){
        	case "Search Id":
        			Subscriber subscriber = null;
        			subscriberList = new ArrayList<Subscriber>();
					if(!(searchText.contentEquals("") || searchText.isEmpty())) {
	        			subscriber = ClientUI.chat.requestServerForSubscriber(searchText);
	        			subscriberList.add(subscriber);
					}
        		break;
        	case "Search by Name":
        		subscriberList  = ClientUI.chat.requestServerForSubscriberList("subscriber_name", searchText);
    		break;
        	case "Search by Frozen Status":
        		if(searchText.contentEquals("") || searchText.isEmpty())
        			break;
        		if("frozen".contains(searchText.toLowerCase())) {
            		subscriberList  = ClientUI.chat.requestServerForSubscriberList("subscriber_frozen", "1");
        		}
        		else if("active".contains(searchText.toLowerCase())){
        			subscriberList  = ClientUI.chat.requestServerForSubscriberList("subscriber_frozen", "0");
        		}
        		else {
        			subscriberList = new ArrayList<Subscriber>();
        		}
    		break;
        	default:
        		break;
        }
        if(subscriberList == null) {
        	subscriberList = new ArrayList<Subscriber>();
        }
        
        observableSubscribers.clear();
        observableSubscribers.addAll(subscriberList);
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
    		IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
    		if(genericController instanceof SubscriberInfoFrameController) {
    			SubscriberInfoFrameController infoController = (SubscriberInfoFrameController)genericController;
    			infoController.setObject((Subscriber)selectedSubscriber);
    			infoController.initializeFrame();
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

	@Override
	public void initializeFrame() {
		searchOptions.getItems().addAll("Search Id", "Search by Name", "Search by Frozen Status");
        searchOptions.setValue("Search Id");
		initializeTable();
		
	}

	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}

	
	
}
