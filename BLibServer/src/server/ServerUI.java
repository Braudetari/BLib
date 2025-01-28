package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import controller.NotificationController;
import gui.ServerPortFrameController;
import server.BLibServer;

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	final public static String DEFAULT_DB_IP = null;
	final public static String DEFAULT_DB_SCHEME = null;
	final public static String DEFAULT_DB_USER = null;
	final public static String DEFAULT_DB_PASS = null;
	public static String DB_IP = DEFAULT_DB_IP;
	public static String DB_SCHEME = DEFAULT_DB_SCHEME;
	public static String DB_USER = DEFAULT_DB_USER;
	public static String DB_PASS = DEFAULT_DB_PASS;
	static BLibServer server;
	private static boolean listenNotifiers;
	
	public static void main( String args[] ) throws Exception
	   {   
		//put arguments as DB settings
		try {
			if(args[0].contains("?") || args[0].contains("help")){
				String help = "<How to use BLibServer (G13)>\n"
						+ "arguments: [DB_IP] [DB_SCHEME] [DB_USER] [DB_PASS]" + "\n"
						+ "or leave empty/partial for local database.db file" + "\n"
						+ "place database.db in the same folder as BLibServer" + "\n";
				System.out.println(help);
				System.exit(0);
			};
		}
		catch(Exception e) {
			//Nothing is wrong here
		}
		try {
			DB_IP = args[0];
			DB_SCHEME = args[1];
			DB_USER = args[2];
			DB_PASS = args[3];
			System.out.println("DB arguments accepted.");
		}
		catch(Exception e) {
			System.out.println("DB arguments incomplete/failed. using default (local db)");
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
		primaryStage.setOnCloseRequest(event -> {
			System.out.println("Server stopped: quit using X");
			Platform.exit();
			System.exit(0);
		});
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
	        	if(DB_IP != null)
	        		BLibServer.dbConnection = DatabaseConnection.getInstance(DB_IP, DB_SCHEME, DB_USER, DB_PASS);
	        	else
	        		BLibServer.dbConnection = DatabaseConnection.getInstance();
	        	
	    		//Schedule Notifiers
	    		ServerUI.listenNotifiers = true;
	    		Thread notifiersThread = new Thread(() -> {
	    			NotifiersListener();
	    		});
	    		notifiersThread.setDaemon(true);
	    		notifiersThread.start();
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
			ServerUI.listenNotifiers = false;
			server.close();
			System.exit(0);
			
		} catch (IOException e) {
			System.out.println("ERROR - could not close server");
			System.exit(0);
		}
	}
	
	/**
	 * Listen to Notifiers, perform the following tasks every day
	 * Remind Subscribers a day before return date to return books
	 * Unfreeze Subscribers after a month
	 * Remove Reservations after Two Days of not Borrowing
	 */
	private static void NotifiersListener() {
		try {
			LocalDate dateOld = null;
			while(ServerUI.listenNotifiers == true) {
				LocalDate dateNow = LocalDate.now();
				if(dateOld == null || dateNow.isAfter(dateOld)) { //if new dawn
					//Run Notifiers
					Platform.runLater(() -> {
						NotificationController.BorrowReminderDayBefore(BLibServer.dbConnection.getConnection());
						NotificationController.UnfreezeAfterAMonth(BLibServer.dbConnection.getConnection());
						NotificationController.ReservationRemovalAfterTwoDays(BLibServer.dbConnection.getConnection());
					});
				}
				Thread.sleep(3600000); //sleep for hour
			}
		}
		catch(Exception e) {
			System.err.println("Could not listen to Notifiers");
		}
	}


}
