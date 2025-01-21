// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import client.*;
import common.Message;
import common.Subscriber;
import gui.NoticeFrameController;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
  public ConnectionStatus status;
  public static enum ConnectionStatus{Disconnected, Connected}; 
  private List<Subscriber> subscriberList;
  private Subscriber subscriber;
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
	  System.out.println("--> " + msg);
	  try {
		  Message message = Message.decrypt((Message)msg, this.sessionId);
		  if(message != null) {
			  lastResponse = message.getRequest();
			  switch(message.getRequest()) {
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
			  	//Display error as Notice
			  	case "error":
			  			NoticeFrameController frame = new NoticeFrameController();
			  			frame.start(message.getMessage());
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
}
//End of ChatClient class
