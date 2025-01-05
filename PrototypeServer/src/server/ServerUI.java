package server;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Vector;
import gui.ServerPortFrameController;
import server.BLibServer;

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	final public static String DEFAULT_DB_IP = "localhost";
	final public static String DEFAULT_DB_SCHEME = "prototypedb";
	final public static String DEFAULT_DB_USER = "root";
	final public static String DEFAULT_DB_PASS = "Aa123456";
	public static String DB_IP = DEFAULT_DB_IP;
	public static String DB_SCHEME = DEFAULT_DB_SCHEME;
	public static String DB_USER = DEFAULT_DB_USER;
	public static String DB_PASS = DEFAULT_DB_PASS;
	static BLibServer server;
	
	public static void main( String args[] ) throws Exception
	   {   
		//put arguments as DB settings
		try {
			DB_IP = args[0];
			DB_SCHEME = args[1];
			DB_USER = args[2];
			DB_PASS = args[3];
			System.out.println("DB arguments accepted.");
		}
		catch(Exception e) {
			System.out.println("DB arguments failed. using default");
			DB_IP = DEFAULT_DB_IP;
			DB_SCHEME = DEFAULT_DB_SCHEME;
			DB_USER = DEFAULT_DB_USER;
			DB_PASS = DEFAULT_DB_PASS;
		}
		 launch(args);
	  } // end main
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub				  		
		ServerPortFrameController ServerPortFrame = new ServerPortFrameController(); // create StudentFrame
		ServerPortFrame.start(primaryStage);
	}
	
	public static BLibServer runServer(String p)
	{
		 int port = 0; //Port to listen on

	        try
	        {
	        	port = Integer.parseInt(p); //Set port	          
	        }
	        catch(Throwable t)
	        {
	        	System.out.println("ERROR - Could not connect!");
	        }
	    	
	        server = new BLibServer(port);
	        try {
		        server.dbConnection = DatabaseConnection.getInstance(DB_IP, DB_SCHEME, DB_USER, DB_PASS);
		        
		        
	        }
	        catch(Exception e) {
	        	System.out.println("ERROR - could not connect to DB");
	        	stopServer();
	        	return null;
	        }
	        try 
	        {
	          server.listen(); //Start listening for connections
	        } 
	        catch (Exception ex) 
	        {
	          System.out.println("ERROR - Could not listen for clients!");
	          stopServer();
	          return null;
	        }
	        return server;
	}
	
	public static void stopServer() {
		
		server.stopListening();
		try {
			
			server.close();
			System.exit(0);
			
		} catch (IOException e) {
			System.out.println("ERROR - could not close server");
			System.exit(0);
		}
	}
	

}
