// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import client.*;
import common.*;
import gui.NoticeFrameController;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static boolean awaitResponse = false;
  String lastResponse = null;
  String lastResponseError = null;
  String lastResponseMsg = null;
  public ConnectionStatus status;
  public static enum ConnectionStatus{Disconnected, Connected};
  //Storage for Objects from Server
  String sessionId; //sessionId
  String name; //Name recieved for User
  User user; //User from server
  List<Subscriber> subscriberList; //subscriberList received from server
  Subscriber subscriber; //subscriber received from server
  List<Book> books; //books list receieved from server 
  Book book; //book received from server
  DetailedHistory history; //detailed history received from server
  List<DetailedHistory> historyList; //detailed history LIST received from server
  List<Notification> notifications;
  Object[] bookAvailibilityInfo;
  Object data;
  int intResponse;
  List<BorrowedBook> borrowedBooks; 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
	 
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.status = ConnectionStatus.Disconnected;
    //openConnection();
  }

  //Instance methods ************************************************
   
  /**
  * Handles incoming data from the server by decrypting the message and 
  * processing it based on the request type. Each request type updates 
  * specific local fields or sets error/state variables as needed.
  *
  * <p>Possible request types and their effects:</p>
  * <ul>
  *   <li><b>"msg"</b>: Expects a {@code String} message from the server; 
  *       stored in {@code lastResponseMsg}.</li>
  *   <li><b>"subscribers"</b>: Expects a {@code List<Subscriber>} containing subscriber records; 
  *       stored in {@code subscriberList}.</li>
  *   <li><b>"getsubscriber"</b>: Expects a single {@code Subscriber} record; 
  *       stored in {@code subscriber}.</li>
  *   <li><b>"connected"</b>: Expects a {@code String} session ID; 
  *       stored in {@code sessionId}.</li>
  *   <li><b>"login"</b>: Expects a {@code String} that, when split on `;`, 
  *       contains a serialized {@code User} plus an associated name. 
  *       The {@code User} is parsed and stored in {@code user}, the name in {@code name}.</li>
  *   <li><b>"book"</b>: Expects a serialized {@code Book} record; 
  *       deserialized and stored in {@code book}.</li>
  *   <li><b>"books"</b>: Expects a {@code List<Book>} containing multiple Book records; 
  *       stored in {@code books}.</li>
  *   <li><b>"bookinfo"</b>: Expects an {@code Object[]} with book availability info; 
  *       stored in {@code bookAvailibilityInfo}.</li>
  *   <li><b>"isbookreserved"</b>: Expects a {@code String} representation of an integer; 
  *       parsed and stored in {@code intResponse} to indicate if a book is reserved.</li>
  *   <li><b>"history"</b>: Expects a serialized {@code String} representing a list of 
  *       {@code DetailedHistory} records; deserialized and stored in {@code historyList}.</li>
  *   <li><b>"borrowedbooks"</b>: Expects a {@code List<BorrowedBook>} containing borrowed-book records; 
  *       stored in {@code borrowedBooks}.</li>
  *   <li><b>"notifications"</b>: Expects a {@code List<Notification>} containing user notifications; 
  *       stored in {@code notifications}.</li>
  *   <li><b>"data"</b>: Expects any general {@code Object}; 
  *       stored in {@code data} for later processing.</li>
  *   <li><b>"error"</b>: Expects a {@code String} describing an error; 
  *       stored in {@code lastResponseError}.</li>
  * </ul>
  *
  * @param msg the raw message object (typically of type {@code Message}) received from the server
  */
  public void handleMessageFromServer(Object msg) 
  {
	  try {
		  Message message = Message.decrypt((Message)msg, this.sessionId);
		  System.out.println("--> " + message.getRequest() + " " + message.getMessage());
		  if(message != null) {
			  lastResponse = message.getRequest();
			  switch(message.getRequest()) {
			   case "msg":
				   lastResponseMsg = (String)message.getMessage();
				  break;
			  	case "subscribers":
			  			try {
			  				this.subscriberList = (List<Subscriber>)message.getMessage();
			  			}
			  			catch(Exception e) {
			  				System.err.println("Could not receive subscriber List from Server");
			  			}
			  		break;
			  	case "getsubscriber":
			  		try {
				  		subscriber=(Subscriber)message.getMessage();
			  		}
			  		catch(Exception e) {
			  			System.err.println("Could not receive subscriber from Server");
			  		}
			  		break;
			  	case "connected":
			  			this.sessionId = (String)message.getMessage();
			  			break;
			  			
			  	case "login":
			  		try{
			  			String[] split = ((String)message.getMessage()).split(";");
			  			this.user = User.fromString(split[0]);
			  			this.name = split[1];
			  		}
			  		catch(Exception e) {
			  			System.err.println("Could not receive user and name from server.");
			  		}
			  		break;
			  		
			  	case "book":
			  			try {
			  				this.book = Book.fromString((String)message.getMessage());
			  			}
			  			catch(Exception e) {
			  				e.printStackTrace();
			  				System.err.println("Could not receive book from server");
			  			}
			  		break;
			  	
			  	case "books":
				  		try {
			  				this.books = (List<Book>)message.getMessage();
			  			}
			  			catch(Exception e) {
			  				e.printStackTrace();
			  				System.err.println("Could not receive books list from server");
			  			}
			  		break;
			  	case "bookinfo":
			  			try {
			  				this.bookAvailibilityInfo = (Object[])message.getMessage();
			  			}
			  			catch(Exception e) {
			  				e.printStackTrace();
			  				System.err.println("Could not receive book availibility info from server");
			  			}
			  		break;
			  	case "isbookreserved":
		  			try {
		  				this.intResponse = Integer.parseInt((String)message.getMessage());
		  			}
		  			catch(Exception e) {
		  				e.printStackTrace();
		  				System.err.println("Could not receive wheter book is reservable from server");
		  			}
		  		break;
			  	case "history":
			  		try {
			  			this.historyList = DetailedHistory.detailedHistoryListFromString((String)message.getMessage());
			  		}
			  		catch(Exception e) {
			  			System.err.println("Could not receive history list from server");
			  		}
			  		break;
			  	case "borrowedbooks":
			  			try {
			  				this.borrowedBooks = (List<BorrowedBook>)message.getMessage();
			  			}
			  			catch(Exception e) {
			  				System.err.println("Could not receive borrowed book list from server");
			  			}
			  		break;
			  	case "notifications":
			  		try {
			  			this.notifications = (List<Notification>)message.getMessage();
			  		}catch(Exception e) {
			  			System.err.println("Could not receive notifications");
			  		}
			  		break;
			  	case "data":
			  		try {
			  			this.data = message.getMessage();
			  		}
			  		catch(Exception e) {
			  			e.printStackTrace();
			  			System.err.println("Could not receive data");
			  		}
			  		break;
			  	//Display error as Notice
			  	case "error":
			  			lastResponseError = (String)message.getMessage();
			  			break;
			  	default:
			  		break;
			  }
		  }
		  
		  awaitResponse = false;
	  }
	  catch(Exception e) {
		  e.printStackTrace();
		  System.err.println("Could not cast msg as Message object");
	  }
  }
  
  /**
   * Return Connect to Server Message
   * @return Message
   */
  Message getConnectMessage() {
	  try {
		  String hostname = InetAddress.getLocalHost().getHostName();
		  Message message = new Message("connect", null, hostname);
		  return message;
	  }
	  catch (Exception e){
		  e.printStackTrace();
		  System.err.println("Could not get client hostname");
		  return null;
	  }
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(Message message)  
  {
    try
    {
    	openConnection();//in order to send more than one message
       	awaitResponse = true;
       	//Encrypt message and send to server
       	if(message.getMessage() == null)
       		message.setMessage("");
       	sendToServer(Message.encrypt(message));
		// wait for response
		while (awaitResponse) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		status = ConnectionStatus.Connected;
    }
    catch(IOException e)
    {
    	e.printStackTrace();
      clientUI.display("Could not send message to server: Terminating client."+ e);
      quit();
    }
  }

  //Client-package private access for SessionId
  String getSessionId() {
	  return this.sessionId;
  }
  
  void setSessionId(String sessionId) {
	  this.sessionId = sessionId;
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    //System.exit(0);
  }
  
}
//End of ChatClient class
