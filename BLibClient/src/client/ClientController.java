// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package client;
import java.io.*;
import java.net.InetAddress;
import java.util.List;

import client.*;
import common.Client;
import common.Message;
import common.Subscriber;
import common.loginInfo;


/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientController implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
   public static int DEFAULT_PORT ;
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientController(String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"+ " Terminating client.");
      System.exit(1);
    }
  }

  
  //Instance methods ************************************************
  
  public List<Subscriber> requestSubscribersFromServer(){
	  Message message = new Message("subscribers", client.getSessionId(), null);
	  accept(message);
	  return client.getSubscriberList();
  }
  public Subscriber requestSubscriberFromServer(String id){
	  Message message = new Message("getsubscriber", client.getSessionId(), id);
	  accept(message);
	  //CHANGE
	  //Try-Catch if accept fails
	  return client.getSubscriber();
  }
  public Client requestClientFromServer(loginInfo info) {
	    Message message = new Message("login", null, info.toString());
	    try {
	        accept(message);
	    } catch (Exception e) {
	        // Handle the exception, for example, by logging or rethrowing it
	        System.err.println("Error while sending the message to the server: " + e.getMessage());
	        e.printStackTrace(); // Optional: To debug the stack trace
	        return null; // Return null or handle the error appropriately
	    }
	    return client.getClient();
	}
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
   public void accept(Message msg) 
  {
	  Message message = new Message(msg);
	  message.setSessionId(client.getSessionId());
	  client.handleMessageFromClientUI(message);
  }
  
  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }
  
  public ChatClient.ConnectionStatus getConnectionStatus(){
	  ChatClient.ConnectionStatus status = client.status;
	  return status;
  }
  
  public boolean connect() {
		  Message msg = client.getConnectMessage();
		  if(msg == null)
			  return false;
		  accept(msg);
		  return true;
  }
  
}
//End of ConsoleChat class
