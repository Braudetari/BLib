// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import controller.*;
import java.io.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import common.Message;
import common.Subscriber;
import common.User;
import ocsf.server.*;
import server.ConnectionToClientInfo.ClientConnectionStatus;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class BLibServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  //final public static int DEFAULT_PORT = 5555;
  private static ArrayList<ConnectionToClientInfo> clientConnections = new ArrayList<ConnectionToClientInfo>();
  private static Thread threadPing;
  private boolean flagKillPingThread = false;
  static DatabaseConnection dbConnection; //package-private
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * 
   */

  public BLibServer(int port) 
  {
    super(port);
  }

  //Instance methods ************************************************
  
  //return connections as readonly for viewing
  public List<ConnectionToClientInfo> getClientConnectionsList(){
	  return Collections.unmodifiableList(clientConnections);
  }
  
  private boolean isClientInList(ConnectionToClient client) {
	  for(ConnectionToClientInfo clientInfo : clientConnections) {
		  if(clientInfo.getClient().equals(client)) {
			  return true;
		  }
	  }
	  return false;
  }
  
  private ConnectionToClientInfo getClientInList(ConnectionToClient client) {
	  for(ConnectionToClientInfo clientInfo : clientConnections) {
		  if(clientInfo.getClient().equals(client)) {
			  return clientInfo;
		  }
	  }
	  return null;
  }
  
  private void handleClientConnection(ConnectionToClient client, String clientName, User clientUser) {
	  boolean clientExists = false;
	  int clientIndex = -1;
	  for(int i=0; i<clientConnections.size() && clientExists == false; i++) {
		  ConnectionToClientInfo clientInfo = clientConnections.get(i);
		  if(clientInfo.equals(client, clientName)) {
			  clientExists = true;
			  clientIndex = i;
		  }
	  }
	  if(!clientExists) {
		  clientConnections.add(new ConnectionToClientInfo(client, clientName, clientUser));
	  }
	  else {
		  if(clientIndex >= 0) {
			  clientConnections.get(clientIndex).setClient(client);
			  clientConnections.get(clientIndex).setStatus(ClientConnectionStatus.Connected);
		  }
	  }
  }
  
  private String generateSessionId() {
	  return UUID.randomUUID().toString();
  }
  
  private void handleClientDisconnection(ConnectionToClientInfo clientInfo) {
	  clientInfo.setStatus(ClientConnectionStatus.Disconnected);
  }
  
  /**
   * Pings all clients known for isAlive()
   * If fails MAX_TIMEOUTS in a row will disconnect client
   * @throws InterruptedException
   */
  public void pingConnections() throws InterruptedException {
	  while(true) {
		  if(flagKillPingThread)
			  	return; 
		  int connectionsSize = clientConnections.size();
		  for(int i=0; i<connectionsSize; i++) {
			  ConnectionToClientInfo clientInfo = clientConnections.get(i);
			  if(!clientInfo.getClient().isAlive() && clientInfo.getStatus() == ClientConnectionStatus.Connected){
				  handleClientDisconnection(clientInfo);
				  connectionsSize--; //relic of old when we used to delete our connections
			  }
		  }
		  Thread.sleep(1000);
	  }
	  
  }
  
  public void handleMessageToClient(Object msg, ConnectionToClient client) {
	  try {
		  Message message = (Message)msg;
		  client.sendToClient(Message.encrypt(message));
	  }
	  catch(Exception e) {
		  e.printStackTrace();
	  }
  }
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   * @param 
   */
  public void handleMessageFromClient  (Object msg, ConnectionToClient client)
  {
	 System.out.println("Message received: " + msg + " from " + client);
	 ConnectionToClientInfo clientInfo = getClientInList(client);
	 String clientSessionId = null;
	 if(clientInfo != null)
		 clientSessionId = clientInfo.getSessionId();
	 try {
		 Message message = Message.decrypt((Message)msg, clientSessionId);
		 System.out.println("Decrypted message: " + message + " from " + client);
		 if(message == null)
			 return;
		 Message reply;
		 Subscriber subscriber;
		 String str;
		 switch(message.getRequest()) {
		 	case "connect":
				 handleClientConnection(client, message.getMessage(), null);
				 clientInfo = getClientInList(client);
				 String sessionId = clientInfo.getSessionId();
				 if(sessionId == null) {
					 clientInfo.setSessionId(generateSessionId());
				 }
				 reply = new Message("connected", null, clientInfo.getSessionId());
				 handleMessageToClient(reply, client);
		 		break;
		 	
		 	case "subscribers":
		 		List<Subscriber> subscriberList = SubscriberController.getAllSubscribers(dbConnection.getConnection());
		 		reply = new Message("subscribers", clientInfo.getSessionId(),Subscriber.subscriberListToString(subscriberList));
		 		handleMessageToClient(reply, client);
		 		break;
		 		
		 	case "updatesubscriber":
		 		//Update subscriber in DB sent from client in string form
		 		subscriber = Subscriber.subscriberFromString(message.getMessage());
		 		SubscriberController.updateSubscriberInfo(dbConnection.getConnection(), subscriber.getSubscriberId(), subscriber.getSubscriberEmail(), subscriber.getSubscriberPhoneNumber());
		 		reply = new Message("msg",clientInfo.getSessionId(),"updated subscriber");
		 		handleMessageToClient(reply, client);
		 	break;
		 	case "getsubscriber":
		 		int subscriberId;
		 		try {
			 		subscriberId = Integer.parseInt(message.getMessage());
		 		}
		 		catch(Exception e) {
		 			e.printStackTrace();
		 			System.err.println("Could not parse Integer of SubscriberId");
		 			reply = new Message("error", clientInfo.getSessionId(), "SubscriberId is not an integer.");
		 			handleMessageToClient(reply, client);
		 			break;
		 		}
		 		subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriberId);
		 		if(subscriber == null)
		 			subscriber = new Subscriber();
		 		reply = new Message("getsubscriber", clientInfo.getSessionId(), subscriber.toString());
		 		handleMessageToClient(reply, client);
		 		break;
		 		
		 	//Handle client login request, e.g. request:"login", msg:"username password"
		 	case "login":
		 			str = message.getMessage();
		 			User user = null;
		 			if(str.equals("guest")) { //user logged in as guest
		 				user = new User(0, null, null, User.UserType.GUEST);
		 			}
		 			else { //User isn't logging in as guest
			 			String[] loginInfo = str.split(" ");
			 			String username = loginInfo[0];
			 			String password = loginInfo[1];
			 			//try to find user in database
			 			user = UserController.getUserByUsername(dbConnection.getConnection(), username);
			 			Message replyLoginFail = new Message("error", clientInfo.getSessionId(), "Incorrect username or password");
			 			if(user == null) {  //username doesnt exist
			 				handleMessageToClient(replyLoginFail, client);
			 				break;
			 			}
			 			if(!password.equals(user.getPassword())) { //incorrect password
			 				handleMessageToClient(replyLoginFail, client);
			 				break;
			 			}
		 			}
		 			clientInfo.setUser(user);
		 		break;
		 		
		 	default:
		 		return;
		 }
		 if(!isClientInList(client)) {
			 reply = new Message("requestConnect", null, null);
			 handleMessageToClient(reply, client);
		 }
	 }
	 catch(Exception e) {
		 e.printStackTrace();
	 }
  }
   
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println ("Server listening for connections on port " + getPort());
    //Start pinging all known and new connections
    threadPing = new Thread(() -> {
		try {
			pingConnections();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	});
    threadPing.start();
  }
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()  {
    System.out.println ("Server has stopped listening for connections.");
    
    flagKillPingThread = true;
  }
  
}
//End of EchoServer class
