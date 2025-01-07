package gui;

import java.io.IOException;

import client.ClientUI;
import common.Message;
import common.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SubscriberInfoFrameController {

	@FXML
	private Button btnClose = null;
	@FXML
	private Button btnUpdate = null;
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
	private static Subscriber importedSubscriber;
	
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
		editedSubscriber.setSubscriberEmail(txtEmail.getText());
		editedSubscriber.setSubscriberPhoneNumber(txtPhone.getText());
		ClientUI.chat.accept(new Message("updatesubscriber",null,editedSubscriber.toString()));
		getCloseButton(event); //close post update
	}
	
	@FXML
	private void getCloseButton(ActionEvent event) throws Exception {
		((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //exit window
	}
	
	
	
	
}
