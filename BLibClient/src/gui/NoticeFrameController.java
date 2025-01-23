package gui;

import java.io.IOException;

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

public class NoticeFrameController {
	
	private final static String title = "Notice";
	
	@FXML
	private Button btnOK = null;
	
	@FXML
	private Label labelmsg;
	
	public void loadText(String text) {
		labelmsg.setText(text);
	}
	
	public void start(String noticeMessage) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/NoticeFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/styleNotice.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);		
		primaryStage.show();
		NoticeFrameController controller = loader.getController();
		controller.loadText(noticeMessage);
	}
	
	
	@FXML
	public void getOKBtn(ActionEvent event) throws Exception {
		//Close the window
		Stage thisStage = (Stage)((Node)event.getSource()).getScene().getWindow();
		thisStage.close();
	}
	
}
