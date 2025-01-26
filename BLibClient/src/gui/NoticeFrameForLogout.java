package gui;

import java.io.IOException;

import client.ClientUI;
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

public class NoticeFrameForLogout {
	
	private final static String title = "Notice";
	
	@FXML
	private Button btnConfirm = null;
	
	@FXML
	private Button btnCancel = null;
	
	@FXML
	private Label labelmsg;
	
	private Stage parentStage;
	
	public void loadText(String text) {
		labelmsg.setText(text);
	}
	
	public void setParentStage(Stage parentStage) {
		this.parentStage=parentStage;
	}

	public void start(Stage parentStage) throws IOException{
		//this.parentStage=parentStage;
		FXMLLoader loader = new FXMLLoader();
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/NoticeFrameForLogout.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/NoticeFrameForLogout.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);		
		primaryStage.show();
		NoticeFrameForLogout controller = loader.getController();
		controller.setParentStage(parentStage);
		controller.loadText("Are you sure you want to Logout?");
	}
	
	
	@FXML
	public void getCancelBtn(ActionEvent event) throws Exception {
		//Close the window
		Stage thisStage = (Stage)((Node)event.getSource()).getScene().getWindow();
		thisStage.close();
	}
	
	@FXML
	public void getConfirmBtn(ActionEvent event) throws Exception {
		//Close the MenuUI
		Stage thisStage = (Stage)((Node)event.getSource()).getScene().getWindow();
		thisStage.close();
		//Logout from server
		ClientUI.chat.LogoutFromServer();
		//return to the LoginUI
		(new LoginFrameController()).start(parentStage);
	}
	
}
