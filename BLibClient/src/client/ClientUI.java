package client;
import javafx.application.Application;

import javafx.stage.Stage;
import java.util.Vector;
import gui.ConnectionFrameController;
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
		// TODO Auto-generated method stub
						  		
		 ConnectionFrameController connectionFrameController = new ConnectionFrameController(); // create StudentFrame
		connectionFrameController.start(primaryStage, ConnectionIP, port);
	}
	
}
