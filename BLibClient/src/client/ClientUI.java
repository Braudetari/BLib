package client;
import javafx.application.Application;

import javafx.stage.Stage;
import java.util.Vector;
import gui.ConnectionFrameController;
import gui.LoginFrameController;
import gui.NoticeFrameController;
import gui.ScreenLoginController;
import client.ClientController;
import common.Message;

public class ClientUI extends Application {
	public static ClientController chat; //only one instance
	private static String ConnectionIP;
	private static int port;
	
	public static void main( String args[] ) throws Exception
	   { 
			try {
				ConnectionIP = args[0]; //first argument should be IP
			}
			catch(Exception e) {
				ConnectionIP = "localhost";
			}
			
			try {
				port = Integer.parseInt(args[1]); //second argument should be port
				
			}
			catch(Exception e) {
				port = 5555;
			}
		    launch(args); 
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated meWthod stub
		ClientUI.chat = new ClientController(ConnectionIP, port);
		ClientUI.chat.connect();
		if(ClientUI.chat.getConnectionStatus().toString().equals("Connected")) {
			(new LoginFrameController()).start(primaryStage);
		}
		else {
			(new NoticeFrameController()).start("Could not connect to server "+ConnectionIP+":"+port);
		}
		
	}
	
}
