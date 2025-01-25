package client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
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
            //Close program if pressed on 'X'
            primaryStage.setOnCloseRequest(event -> {
            	System.out.println("Client stopped: quit using X");
    			Platform.exit();
    			System.exit(0);
    		});
            (new LoginFrameController()).start(primaryStage);
            Thread thread = new Thread(() -> {
            	try {
					ConnectionListener(primaryStage);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Could not start Connection Listener");
				}
            });
            thread.setDaemon(true);
            thread.start();
            
        }
        else {
            (new NoticeFrameController()).start("Could not connect to server "+ConnectionIP+":"+port);
        }
        
    }
    
    /**
     * Listen to Connection, if 
     */
    private void ConnectionListener(Stage primaryStage) throws Exception{
    	boolean connectionActive = true;
		int timeouts = 0;
    	while(connectionActive) {
    		if(ClientUI.chat.client.isConnected()) {
    			timeouts = 0;
    		}
    		else {
    			timeouts++;
    		}
    		Thread.sleep(1000);
    		if(timeouts >= 5) {
    			connectionActive = false;
    		}
    	}
    	Platform.runLater(() -> {
    		if(primaryStage.isShowing()) {
    			primaryStage.close();
    			try {
					(new NoticeFrameController()).start("Disconnected from Server: Connection to server no longer available");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Could not start disconnected Notice");
				}
    		}
    	});
    	//Connection no longer available;
    }
    
}