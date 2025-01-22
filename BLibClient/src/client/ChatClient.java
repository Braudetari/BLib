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
  public static String lastResponse = null;
  public static String lastResponseError = null;
  public static String lastResponseMsg = null;
  public ConnectionStatus status;
  public static enum ConnectionStatus{Disconnected, Connected};
  //Storage for Objects from Server
  private User user;
  private List<Subscriber> subscriberList;
  private Subscriber subscriber;
  private List<Book> books;
  private Book book;
  private DetailedHistory history;
  private List<DetailedHistory> historyList;
  private String sessionId;
  
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

  //More Methods
  
  public List<Subscriber> getSubscriberList(){
	  if(subscriberList == null) {
		  return null;
	  }
	  return Collections.unmodifiableList(subscriberList);
  }
  public Subscriber getSubscriber(){
	  if(subscriber == null) {
		  return null;
	  }
	  return (subscriber);
  }
  //Instance methods ************************************************
   
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
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
				   lastResponseMsg = message.getMessage();
				  break;
			  	case "subscribers":
			  			subscriberList = Subscriber.subscriberListFromString(message.getMessage());
			  		break;
			  	case "getsubscriber":
			  		subscriber=Subscriber.subscriberFromString(message.getMessage());
			  		break;
			  	case "requestConnect":
						try {
							handleMessageFromClientUI(getConnectMessage());
						} catch (Exception e) {
							e.printStackTrace();
						}
			  		break;
			  	case "connected":
			  			this.sessionId = message.getMessage();
			  			break;
			  			
			  	case "login":
			  		try{
			  			this.user = User.fromString(message.getMessage());
			  		}
			  		catch(Exception e) {
			  			System.err.println("Could not receive user from server.");
			  		}
			  		break;
			  		
			  	case "book":
			  			try {
			  				this.book = Book.fromString(message.getMessage());
			  			}
			  			catch(Exception e) {
			  				e.printStackTrace();
			  				System.err.println("Could not receive book from server");
			  			}
			  		break;
			  	
			  	case "books":
				  		try {
			  				this.books = Book.bookListFromString(message.getMessage());
			  			}
			  			catch(Exception e) {
			  				e.printStackTrace();
			  				System.err.println("Could not receive books list from server");
			  			}
			  		break;
			  	case "history":
			  		try {
			  			this.historyList = DetailedHistory.detailedHistoryListFromString(message.getMessage());
			  		}
			  		catch(Exception e) {
			  			e.printStackTrace();
			  			System.err.println("Could not receive history list from server");
			  		}
			  		break;
			  	//Display error as Notice
			  	case "error":
			  			lastResponseError = message.getMessage();
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
  
  Message getConnectMessage() {
	  try {
		  String hostname = InetAddress.getLocalHost().getHostName();
		  Message message = new Message("connect", getSessionId(), hostname);
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
  
  //DEBUG MAIN
  public static void main(String args[]) {
		ClientController chat = new ClientController("localhost",5555);
		 try {
			chat.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Could not connect to server");
			return;
		}
		 
		 try {
			 Message msg;
			 msg = new Message("login", chat.client.getSessionId(), "wow sauce");
			 chat.client.handleMessageFromClientUI(msg);
			 LocalDate from = LocalDate.of(2025, 1, 20);
			 LocalDate to = LocalDate.of(2025, 1, 29);
			 LocalDate late = LocalDate.of(2025, 1, 29);
			 msg = new Message("getbook", chat.client.getSessionId(), "8");
			 chat.client.handleMessageFromClientUI(msg);
			 msg = new Message("getsubscriber", chat.client.getSessionId(), "4");
			 chat.client.handleMessageFromClientUI(msg);
			 msg = new Message("borrowbook", chat.client.getSessionId(), "102;4;"+DateUtil.DateToString(from)+";"+DateUtil.DateToString(to)+";serial");
			 chat.client.handleMessageFromClientUI(msg);
			 msg = new Message("returnbook", chat.client.getSessionId(), chat.client.book.toString()+";"+chat.client.subscriber.toString()+";"+DateUtil.DateToString(LocalDate.now()));
			 chat.client.handleMessageFromClientUI(msg);
			 String lr = chat.client.lastResponse;
			 String lre = chat.client.lastResponseError;
			 String lrm = chat.client.lastResponseMsg;
			 System.out.println(lr +" ; "+ lre + " ; " + lrm);
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 try {
			chat.client.closeConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
}
//End of ChatClient class
