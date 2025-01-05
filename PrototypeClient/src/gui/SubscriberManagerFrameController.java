package gui;

import java.io.IOException;
import java.util.List;

import client.ClientUI;
import common.Subscriber;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SubscriberManagerFrameController {

	@FXML
	private Button btnRefresh = null;
	@FXML
	private Button btnExit = null;	
	@FXML
	private Button btnOpen = null;
	@FXML
	private Label lblText = null;
	@FXML
	private TableView<Subscriber> tblSubscribers = null;
	@FXML
	private TableColumn<Subscriber, Integer> tblColumnId = null;
	@FXML
	private TableColumn<Subscriber, String> tblColumnName = null;
	@FXML
	private Button btnShowAll=null;
	@FXML
	private Button getStudentbtn=null;
	private final ObservableList<Subscriber> observableSubscribers = FXCollections.observableArrayList();
	private List<Subscriber> subscriberList;
	private Subscriber selectedSubscriber=null;
	private static boolean flagKillInfoListenThread = false;
	private void refreshSubscriberList() {
		subscriberList = ClientUI.chat.requestSubscribersFromServer();
	}
	
	@FXML
	private void initializeTable() {
		refreshSubscriberList();
		
		if(subscriberList != null) {
			observableSubscribers.addAll(subscriberList);
		}
		
		tblColumnId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberId()));
		tblColumnName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberName()));
        tblSubscribers.setItems(observableSubscribers);
	}
	
	public void start(Stage primaryStage) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/SubscriberManagerFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberManagerFrame.css").toExternalForm());
		primaryStage.setTitle("Subscriber Management");
		primaryStage.setScene(scene);		
		primaryStage.show();
		
		SubscriberManagerFrameController controller = loader.getController();
		//controller.initializeTable();
	}
	
	@FXML
	private void Open(ActionEvent event) throws Exception{
		SubscriberInfoFrameController subscriberInfoFrame = new SubscriberInfoFrameController();
		Stage infoStage = new Stage();
		subscriberInfoFrame.start(infoStage, selectedSubscriber);
		flagKillInfoListenThread = false;
		Thread listener = new Thread(() -> {
			try {
				infoClosedListener(infoStage, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	    listener.start();
		
	}
	
	private void infoClosedListener(Stage infoStage, ActionEvent event) throws Exception {
		while(infoStage.isShowing()) {
			if(flagKillInfoListenThread)
				return;
			Thread.sleep(200);
		}
		Refresh(event);
	}
	
	@FXML
	private void Exit(ActionEvent event) throws Exception {
		flagKillInfoListenThread = true;
		((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close window
		System.exit(0); //close client
	}
	
	@FXML
	protected void Refresh(ActionEvent event) throws Exception{
		refreshSubscriberList();
		if(subscriberList != null) {
			observableSubscribers.clear();
			observableSubscribers.addAll(subscriberList);
		}
		selectedSubscriber = null;
		btnOpen.setDisable(true);
	}
	@FXML
	private void showAll(ActionEvent event) throws Exception {
		refreshSubscriberList();
		if(subscriberList != null) {
			observableSubscribers.clear();
		}
		initializeTable();
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
	    SubscriberSearchFrameController subscriberManagerFrame = new SubscriberSearchFrameController();
	    subscriberManagerFrame.start(new Stage());
	    
	}

	
	
}
