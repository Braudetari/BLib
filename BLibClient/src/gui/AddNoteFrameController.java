package gui;

import java.io.IOException;
import java.time.LocalDate;

import client.ClientUI;
import common.DetailedHistory;
import common.Message;
import common.Subscriber;
import common.User;
import common.User.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AddNoteFrameController implements IController{
	
	@FXML
	private Button btnUpdate = null;
	@FXML
	private TextField txtDescription=null;
	private DetailedHistory importedHistory = null;
	private Subscriber importedSubscriber = null;
	private MenuUIController mainController = null;
	private Integer userHistoryId = 0;

	
	
	public void start(Stage primaryStage, Subscriber subscriber) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/AddNoteFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		primaryStage.setScene(scene);		
		primaryStage.show();
		
	}
	
	@FXML
	private void getUpdateButton(ActionEvent event) throws Exception{
		if(!txtDescription.getText().isEmpty()) {
			DetailedHistory editedHistory=new DetailedHistory(importedHistory);
			editedHistory.setAction(DetailedHistory.ActionType.NOTE);//need to be implemented
			editedHistory.setDescription("Note by librarian "+ ClientUI.chat.getClientName() +": "+ txtDescription.getText() +"--> "+ importedHistory.getDescription());
			editedHistory.setDate(LocalDate.now());
			editedHistory.setUser(null);
			boolean success = ClientUI.chat.requestServerToAddHistoryToSubscriber(editedHistory, importedSubscriber.getDetailedSubscriptionHistory(), importedSubscriber.getSubscriberId());
			if(success) {
				showAlert("Add Note", "Note added to Detailed History");
			}
			else{
				showAlert("Error", "Note could not be added to Detailed History");
			}
		}
		
		Back();
	}

	private void Back() {
		try {
    		IController genericController = mainController.loadFXMLIntoPane("/gui/HistoryFrame.fxml");
    		if(genericController instanceof HistoryFrameController) {
    			HistoryFrameController historyController = (HistoryFrameController)genericController;
    			historyController.setObject(new Object[] {importedSubscriber, (Integer)userHistoryId});
    			historyController.initializeHistory();
    		}
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	}
	
	
	@Override
	public void initializeFrame(Object object) {
		
		
	}

	@Override
	public void initializeFrame() {
		mainController.setPaneTitle("Add Note");
		
	}

	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(Object object) {
		Object[] objectImport = (Object[])object;
		this.importedHistory = (DetailedHistory)objectImport[0];
		this.importedSubscriber = (Subscriber)objectImport[1];
		this.userHistoryId = (Integer)objectImport[2];
		
	}

	@Override
	public void setMainController(MenuUIController controller) {
		this.mainController = controller;
		
	}
	
	   private void showAlert(String title, String message) {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
	
	
	
	
}
