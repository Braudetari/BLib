package gui;

import java.io.IOException;

import client.ClientUI;
import common.Message;
import common.Subscriber;
import common.User;
import common.User.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SubscriberInfoFrameController implements IController{
	
	
	@FXML
	private ToggleButton toggleStatus=null;
	@FXML
	private Button btnClose = null;
	@FXML
	private Button btnUpdate = null;
	@FXML
	private Button btnBack = null;
	@FXML
	private Button btnHistory = null;
	@FXML
	private Label labelmsg=null;
	@FXML
	private TextField txtId=null;
	@FXML
	private TextField txtName=null;
	@FXML
	private TextField txtPhone=null;
	@FXML
	private TextField txtEmail=null;
	private Subscriber importedSubscriber;
	private MenuUIController mainController=null;
	private User.UserType permission=null;
	
	public void initializeSubscriberInfo() {
	    boolean isLibrarian = checkUserType(); // Implement logic to check user type
	    btnBack.setVisible(checkUserType());
	    btnBack.setDisable(!checkUserType());
	    toggleStatus.setSelected(importedSubscriber.isFrozen());
	    if(importedSubscriber.isFrozen()) {
	    	toggleStatus.setText("FROZEN");
	    }
	    else {
	    	toggleStatus.setText("ACTIVE");
	    }
	    toggleStatus.setDisable(!isLibrarian); // Disable for subscribers
	    txtId.setDisable(true);
	    txtName.setDisable(!isLibrarian);
	    loadText(importedSubscriber);
	}

	private boolean checkUserType() {
	    // Replace this with your actual user type check
	    return ClientUI.chat.getClientUser().getType().equals(User.UserType.LIBRARIAN);
	}
	
	private void loadText(Subscriber subscriber) {
		txtId.setText("" + subscriber.getSubscriberId());
		txtName.setText(subscriber.getSubscriberName());
		txtPhone.setText(subscriber.getSubscriberPhoneNumber());
		txtEmail.setText(subscriber.getSubscriberEmail());
	}
	
	public void start(Stage primaryStage, Subscriber subscriber) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/SubscriberInfoFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberInfoFrame.css").toExternalForm());
		primaryStage.setTitle("Subscriber Information");
		primaryStage.setScene(scene);		
		primaryStage.show();
		
		SubscriberInfoFrameController infoFrame = loader.getController();
		importedSubscriber = new Subscriber(subscriber);
		infoFrame.loadText(subscriber);
		
	}
	
	@FXML
	private void getUpdateButton(ActionEvent event) throws Exception{
		Subscriber editedSubscriber = new Subscriber(importedSubscriber);
		if(checkUserType()) { //is librarian
			editedSubscriber.setSubscriberName(txtName.getText());
			editedSubscriber.setFrozen(toggleStatus.isSelected());
		}
		editedSubscriber.setSubscriberEmail(txtEmail.getText());
		editedSubscriber.setSubscriberPhoneNumber(txtPhone.getText());
		ClientUI.chat.requestServerToUpdateSubscriber(editedSubscriber);
	}
	
	@FXML
	private void handleToggleStatus() {
		if (toggleStatus.isSelected()) {
            toggleStatus.setText("FROZEN");
        } else {
        	toggleStatus.setText("ACTIVE");
        }
	}
	
	@FXML
	private void handleHistoryBtn(ActionEvent event) throws Exception{
		try {
    		IController genericController = mainController.loadFXMLIntoPane("/gui/HistoryFrame.fxml");
    		if(genericController instanceof HistoryFrameController) {
    			HistoryFrameController infoController = (HistoryFrameController)genericController;
    			infoController.setObject(new Object[] {importedSubscriber, (Integer)importedSubscriber.getDetailedSubscriptionHistory()});
    			infoController.initializeHistory();
    		}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleBackBtn(ActionEvent event) throws Exception{
		try {
	    		IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml");
	    		if(genericController instanceof HistoryFrameController) {
	    			HistoryFrameController infoController = (HistoryFrameController)genericController;
	    			infoController.setObject(importedSubscriber);
	    			infoController.initializeHistory();
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
	public void setPermission(UserType type) {
		this.permission = type;
		
	}

	@Override
	public void setMainController(MenuUIController controller) {
		this.mainController = controller;
		
	}

	@Override
	public void setObject(Object object) {
		importedSubscriber = (Subscriber)object;
		
	}

	@Override
	public void initializeFrame() {
		mainController.setPaneTitle("Personal Info");
	}
	
	
	
	
}
