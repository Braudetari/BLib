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

public class GuestController {

	@FXML
	private Button btnSearch = null;
	@FXML
	private Button btnBack = null;

	@FXML
	private Pane pane=null;
	@FXML
	private AnchorPane paneScreen=null;
	@FXML
	private AnchorPane paneButtons=null;

	
	
	public void start() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/Guest.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		Stage primaryStage= new Stage();
		primaryStage.setResizable(false);
		primaryStage.setTitle("GuestUI");
		primaryStage.setScene(scene);		
		primaryStage.show();
		GuestController frame = loader.getController();
		
	}
	
	@FXML
	private void getBackBtn(ActionEvent event) {
		try {
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			if (currentStage != null) {
			    currentStage.close();
			}
			(new LoginFrameController()).start(currentStage);
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("couldnt return back from GuestUI to LoginFrame");
		}
	}
	
	
	@FXML 
	private void getSearchBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/SearchBookFrame.fxml");
	}
	
	private void loadFXMLIntoPane(String fxmlFile) {
		try {
			pane.getChildren().clear();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Node node = loader.load();
			Object searchBook = loader.getController();
			if (searchBook instanceof SearchBookFrameController) {
				SearchBookFrameController searchBookController = (SearchBookFrameController) searchBook;
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
