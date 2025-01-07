package gui;

import java.io.IOException;
import java.net.InetAddress;

import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
import common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public  class ConnectionFrameController   {
	private static int itemIndex = 3;
	
	public String title = "Client Connection";
	
	@FXML
	private Button btnExit = null;
	
	@FXML
	private TextField idtxt;
	
	@FXML
	private Button btnConnect;
	
	@FXML
	private TextField serverIPTxt;
	
	@FXML
	private TextField portTxt;
	
	private String getServerIP() {
		return serverIPTxt.getText();
	}
	
	private int getport() {
		return Integer.parseInt(portTxt.getText());
	}
	
	private void initializeText(String ip, String port) {
		serverIPTxt.setText(ip);
		portTxt.setText(port);
	}

	public void start(Stage primaryStage, String ConnectionIP, int ConnectionPort) throws Exception {	
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/gui/ConnectionFrame.fxml").openStream());
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ConnectionFrame.css").toExternalForm());
		ConnectionFrameController controller = loader.getController();
		controller.initializeText(ConnectionIP, "" + ConnectionPort);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();	 	 
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Exiting Client from Connection Screen");
		System.exit(0);
	}
	
	public void getConnectBtn(ActionEvent event) throws Exception{
		
		FXMLLoader loader = new FXMLLoader();
		//Don't leave text empty
		if(this.getServerIP().trim().isEmpty() || (""+this.getport()).trim().isEmpty() ) {
			NoticeFrameController errorNotice = new NoticeFrameController();
			errorNotice.start("Please fill empty text fields!");
		}
		else {
			ClientUI.chat = new ClientController(getServerIP(), getport());
			System.out.println();
			//send connect request with client name
			ClientUI.chat.accept("connect " + Message.encryptToBase64(InetAddress.getLocalHost().getHostName()));
			 if(ClientUI.chat.getConnectionStatus().toString() == "Connected") { //Connection Success
					((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close ConnectionFrame
					SubscriberManagerFrameController subscriberManagerFrame = new SubscriberManagerFrameController();
					subscriberManagerFrame.start(new Stage());
			 }
			 else { //Connection Failed
				 //Open Notice with error
				 String noticeMessage = "Could not connect to Server \""+getServerIP()+":"+getport()+"\"";
				 NoticeFrameController noticeFrameController = new NoticeFrameController();
				 noticeFrameController.start(noticeMessage);
			 }

		}
		
		
		
		//subscriber manager
	}
	
	public  void display(String message) {
		System.out.println("message");
		
	}
	
}
