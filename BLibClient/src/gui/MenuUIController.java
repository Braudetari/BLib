package gui;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import common.Message;
import common.Subscriber;
import common.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MenuUIController {

	@FXML
	private Button btnConnection = null;
	@FXML
	private Button btnManager = null;
	@FXML
	private Pane pane=null;
	@FXML
	private AnchorPane paneScreen=null;
	@FXML
	private AnchorPane paneButtons=null;
	
	private static Subscriber importedSubscriber;
	
	private void initializeButtons() {
		Button[] listOfButtons = {btnConnection, btnManager};
		User.UserType[] buttonPermission = {User.UserType.GUEST, User.UserType.LIBRARIAN};
		int currentButtonPos = 50;
		int buttonPadding = 50;
		
		
	}
	
	public void start( User.UserType permission, String name) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/MenuUI.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		Stage primaryStage= new Stage();
		primaryStage.setResizable(false);
		primaryStage.setTitle("MenuUI");
		primaryStage.setScene(scene);		
		primaryStage.show();
		MenuUIController frame = loader.getController();
		
	}
	
	@FXML
	private void getManagerBtn(ActionEvent event) {
		
	}
	
	@FXML 
	private void getConnectionBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/ConnectionFrame.fxml");
	}
	
	private void loadFXMLIntoPane(String fxmlFile) {
		try {
			pane.getChildren().clear();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Node node = loader.load();
			Object controller = loader.getController();
			if (controller instanceof ConnectionFrameController) {
                ConnectionFrameController connectionController = (ConnectionFrameController) controller;
                // Interact with the controller if needed
                // e.g., connectionController.initializeData(someData);
            }
			pane.getChildren().add(node);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
