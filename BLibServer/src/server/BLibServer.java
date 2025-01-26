// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import controller.*;
import java.io.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import common.*;
import common.DetailedHistory.ActionType;
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
  
  private void handleClientConnection(ConnectionToClient client, Object msg, User clientUser) {
	  String clientName = (String)msg;
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
  
  public void sendMessageToClient(String request, Object msg, ConnectionToClient client, ConnectionToClientInfo clientInfo) {
	  Message reply;
	  if(clientInfo != null)
		  reply = new Message(request, clientInfo.getSessionId(), msg);
	  else {
		  reply = new Message(request, null, msg);
	  }
	  handleMessageToClient(reply, client);
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
	 //System.out.println("Message received: " + msg + " from " + client);
	 ConnectionToClientInfo clientInfo = getClientInList(client);
	 String clientSessionId = null;
	 if(clientInfo != null)
		 clientSessionId = clientInfo.getSessionId();
	 try {
		 Message message = Message.decrypt((Message)msg, clientSessionId);
		 if(message == null) {//handle empty message
			 System.err.println("Empty message");
			 sendMessageToClient("error", "empty message", client, clientInfo);
			 return;
		 }
			 
		 if(clientInfo == null)
			 System.out.println("Message from " + client + "--> request: " + message.getRequest() + " message: " + message.getMessage());
		 else
			 System.out.println("Message from " + clientInfo.getName() + " E--> request: " + message.getRequest() + " message: " + message.getMessage());
		 //Local Variables Storage
		 Message reply;
		 String replyStr;
		 Subscriber subscriber;
		 String str;
		 Book book;
		 BorrowedBook bb;
		 DetailedHistory dh;
		 Object[] object;
		 LocalDate date;
	 	 int subscriberId;
	 	 int result;
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
		 	
		 	case "getsubscribers":
		 		List<Subscriber> subscriberList = SubscriberController.getAllSubscribers(dbConnection.getConnection());
		 		reply = new Message("subscribers", clientInfo.getSessionId(),Subscriber.subscriberListToString(subscriberList));
		 		handleMessageToClient(reply, client);
		 		break;
		 		
		 	case "updatesubscriber":
		 		//Update subscriber in DB sent from client in string form
		 		subscriber = Subscriber.subscriberFromString((String)message.getMessage());
		 		SubscriberController.updateSubscriberInfo(dbConnection.getConnection(), subscriber.getSubscriberId(), subscriber.getSubscriberEmail(), subscriber.getSubscriberPhoneNumber());
		 		reply = new Message("msg",clientInfo.getSessionId(),"updated subscriber");
		 		handleMessageToClient(reply, client);
		 	break;
		 	case "getsubscriber": //expected message: "subscriberId"
		 		try {
			 		subscriberId = Integer.parseInt((String)message.getMessage());
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
		 	case "registersubscriber": //expected message String "username;password;name;email;phone"
		 			try {
		 				str = (String)message.getMessage();
		 				String[] split = str.split(";");
		 				String username = split[0];
		 				String password = split[1];
		 				String name = split[2];
		 				String email = split[3];
		 				String phone = split[4];
		 				object = new Object[] {username, password, name, email, phone};
		 			}
		 			catch(Exception e) {
		 				e.printStackTrace();
		 				System.err.println("Could not parse subscriber info for registration");
		 				sendMessageToClient("error", "Failed to Register: Could not parse subscriber information", client, clientInfo);
		 				break;
		 			}
		 			try { //parse success, try to register
		 				boolean success = SubscriberController.RegisterSubscriber(dbConnection.getConnection(), (String)object[0], (String)object[1], (String)object[2], (String)object[3], (String)object[4]);
		 				if(success) {
		 					sendMessageToClient("msg", "Subscriber " + (String)object[0] + " registered successfuly.", client, clientInfo);
		 				}
		 				else {
		 					throw new Exception();
		 				}
		 				break;
		 			}
		 			catch(Exception e) {
		 				e.printStackTrace();
		 				System.err.println("Could not register subscriber");
		 				sendMessageToClient("error", "Failed to Register: Could not register subscriber " + (String)object[0], client, clientInfo);
		 				break;
		 			}
		 	case "logout":
		 			clientInfo.setUser(null);
		 			reply = new Message("msg", clientInfo.getSessionId(), "logged out from server");
		 			handleMessageToClient(reply, client);
		 		break;
		 	//Handle client login request, e.g. request:"login", msg:"username password"
		 	case "login":
		 			str = (String) message.getMessage();
		 			User user = null;
		 			if(str.toUpperCase().contentEquals("GUEST")) { //user logged in as guest
		 				user = new User(0, "GUEST", null, User.UserType.GUEST);
		 			}
		 			else { //User isn't logging in as guest
		 				try {
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
		 				catch(Exception e) {
		 					System.err.println("Could not parse username and password");
		 					reply = new Message("error", clientInfo.getSessionId(), "Could not parse username and password");
		 					handleMessageToClient(reply, client);
		 					break;
		 				}
		 			}
		 			clientInfo.setUser(user);
		 			String name = UserController.getNameFromUser(dbConnection.getConnection(), user);
		 			reply = new Message("login",clientInfo.getSessionId(), user.toString()+";"+name);
		 			handleMessageToClient(reply, client);
		 		break;
		 		
		 	case "getbook": //return specific book_id
		 			str = (String)message.getMessage();
		 			try {
		 				int bookId = Integer.parseInt(str);
			 			book = BookController.GetBookById(dbConnection.getConnection(), bookId);
			 			reply = new Message("book", clientInfo.getSessionId(), book.toString());
			 			handleMessageToClient(reply, client);
		 			}
		 			catch(Exception e) {
		 				reply = new Message("error", clientInfo.getSessionId(), "bookId "+str+" could not be retreived");
		 				handleMessageToClient(reply, client);
		 			}
		 		break;
		 		
		 	case "getbooks": // get books by element and value
		 		str = (String)message.getMessage();
	 			try {
	 				String[] split = str.split(";");
	 				String element = split[0];
	 				String value = split[1];
		 			List<Book> books = BookController.GetBooksByElement(dbConnection.getConnection(), element, value);
		 			reply = new Message("books", clientInfo.getSessionId(), Book.bookListToString(books));
		 			handleMessageToClient(reply, client);
	 			}
	 			catch(Exception e) {
	 				reply = new Message("error", clientInfo.getSessionId(), str+" are not correct \"element;value\" for books search");
	 				handleMessageToClient(reply, client);
	 			}
		 		break;
		 	case "borrowbook": //expected to get message of "bookSerial/Id;subscriberId;dateFrom;dateTo;id/serial"
		 			User clientUser = clientInfo.getUser();
		 			if(clientUser == null) {
		 				reply = new Message("error", clientInfo.getSessionId(), "You are not logged in.");
		 				handleMessageToClient(reply, client);
		 				break;
		 			}
		 			else if(!clientUser.getType().equals(User.UserType.LIBRARIAN)) {
		 				reply = new Message("error", clientInfo.getSessionId(), "Can't borrow book, not a Librarian.");
		 				handleMessageToClient(reply, client);
		 				break;
		 			}
		 			else {
		 				str = (String)message.getMessage();
		 				try {
		 					//Parse String
			 				String[] split = str.split(";");
			 				int bookOrSerialId = Integer.parseInt(split[0]);
			 				subscriberId = Integer.parseInt(split[1]); 
			 				LocalDate dateFrom = DateUtil.DateFromString(split[2]);
			 				LocalDate dateTo = DateUtil.DateFromString(split[3]);
			 				String borrowType = split[4];
			 				
			 				//Make sure subscriber not frozen
			 				subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriberId);
			 				if(subscriber.isFrozen()) {
			 					reply = new Message("error", clientInfo.getSessionId(), "Access Denied, Subscriber account frozen");
			 					handleMessageToClient(reply, client);
			 					break;
			 				}
			 				//Lend based on serial or id
			 				result = 0;
			 				if(borrowType.equals("serial")) {
			 					result = LendController.LendBookSerialId(dbConnection.getConnection(), subscriberId, dateFrom, dateTo, bookOrSerialId);
			 				}
			 				else if(borrowType.equals("id")) {
			 					result = LendController.LendBookId(dbConnection.getConnection(), subscriberId, dateFrom, dateTo, bookOrSerialId);
			 				}
			 				else {
			 					throw new Exception("Not a valid borrow type");
			 				}
			 				if(result<=0) {
			 					reply = new Message("error", clientInfo.getSessionId(), "Could not borrow book, it might be taken");
			 					handleMessageToClient(reply, client);
			 					break;
			 				}
			 				reply = new Message("msg", clientInfo.getSessionId(), "Book " + bookOrSerialId + " Lent to Subscriber " + subscriberId + " successfully.");
			 				handleMessageToClient(reply, client);
			 				break;
		 				}
		 				catch(Exception e) {
		 					reply = new Message("error", clientInfo.getSessionId(), "Could not borrow book, expected \"bookSerialId;subscriberId;dateFrom;dateTo\"");
		 					handleMessageToClient(reply, client);
		 					break;
		 				}
		 			}
		 	case "returnbook": //expected message : "bookId;returnDate"
		 			str = (String)message.getMessage();
		 			user = clientInfo.getUser();
		 			//Permission Handling: Librarian
		 			if(user == null) {
		 				reply = new Message("error", clientInfo.getSessionId(), "Access denied, not logged in.");
		 				handleMessageToClient(reply, client);
		 				break;
		 			}
		 			if(!user.getType().equals(User.UserType.LIBRARIAN)) {
		 				reply = new Message("error", clientInfo.getSessionId(), "Access denied, not a librarian.");
		 				handleMessageToClient(reply, client);
		 				break;
		 			}
		 			try {
		 				//parse message to objects
			 			String[] split = str.split(";");
			 			book = BookController.GetBookById(dbConnection.getConnection(),Integer.parseInt(split[0]));
			 			date = DateUtil.DateFromString(split[1]);
			 			//return book
			 			result = LendController.ReturnBook(dbConnection.getConnection(), book, date);
			 			//error handling
			 			if(result<=0) {
			 				reply = new Message("error", clientInfo.getSessionId(), "Failed to return book");
			 				handleMessageToClient(reply, client);
			 				break;
			 			}
			 			//success message reply
			 			replyStr = "Returned book successfully";
			 			if(result == 2) {
			 				replyStr += " but return date passed, account was frozen.";
			 			}
			 			reply = new Message("msg", clientInfo.getSessionId(), replyStr);
			 			handleMessageToClient(reply, client);
		 			}
		 			catch(Exception e) {
		 				System.err.println("Failed to parse returnbook message");
		 				reply = new Message("error", clientInfo.getSessionId(), "Failed to parse message to return book");
		 				handleMessageToClient(reply, client);
		 			}
	 			break;
		 	case "gethistory": //expected message "userId"
		 			str = (String)message.getMessage();
		 			try {
		 				int userId = Integer.parseInt(str);
		 				List<DetailedHistory> historyList = DetailedHistoryController.GetHistoryListFromDatabase(dbConnection.getConnection(), userId);
		 				replyStr = DetailedHistory.detailedHistoryListToString(historyList);
		 				reply = new Message("history",clientInfo.getSessionId(), replyStr);
		 				handleMessageToClient(reply, client);
		 			}
		 			catch(Exception e) {
		 				System.err.println("Could not parse message in get history");
		 				reply = new Message("error", clientInfo.getSessionId(), "Could not get history using userId");
		 				handleMessageToClient(reply, client);
		 			}
		 		break;
		 	case "bookinfo": //expected message "bookSerialId"
		 			try {
			 			str = (String)message.getMessage();
			 			book = new Book();
			 			book.setSerial_id(Integer.parseInt(str));
		 				result = BookController.CheckBookSerialAvailability(dbConnection.getConnection(), book.getSerial_id());
		 				if(result>0) {
		 					object = new Object[] {(boolean)true, null}; //send availibility true and returnDate irrelevant
		 				}
		 				else {
		 					date = LendController.GetClosestReturnDateOfBookSerialId(dbConnection.getConnection(), book.getSerial_id());
		 					if(date == null) {
		 						sendMessageToClient("error", "Could not get book availibility info, checking closest Return Date failed", client, clientInfo);
		 					}
		 					object = new Object[] {(boolean)false, date};
		 				}
		 				sendMessageToClient("bookinfo", object, client, clientInfo);
		 			}
		 			catch(Exception e) {
		 				System.err.println("Could not get book info");
		 				sendMessageToClient("error", "Could not get book availibility info", client, clientInfo);
		 			}
		 		break;
		 	case "booksinfo": //expected message "List<Book>"
	 			try {
		 			List<Book> bookList = (List<Book>)message.getMessage();
		 			List<Boolean> availableList = new ArrayList<Boolean>();
		 			List<LocalDate> returnDateList = new ArrayList<LocalDate>();
		 			for(Book bookInList : bookList) {
		 				result = BookController.CheckBookSerialAvailability(dbConnection.getConnection(), bookInList.getSerial_id());
		 				if(result>0) {
		 					returnDateList.add(null);
		 					availableList.add(true);
		 				}
		 				else {
		 					date = LendController.GetClosestReturnDateOfBookSerialId(dbConnection.getConnection(), bookInList.getSerial_id());
		 					if(date == null) {
		 						sendMessageToClient("error", "Could not get book availibility info, checking closest Return Date failed", client, clientInfo);
		 					}
		 					returnDateList.add(date);
		 					availableList.add(false);
		 				}
		 			}
		 			object = new Object[] {availableList, returnDateList};
	 				sendMessageToClient("bookinfo", object, client, clientInfo);
	 			}
	 			catch(Exception e) {
	 				System.err.println("Could not get book info");
	 				sendMessageToClient("error", "Could not get book availibility info", client, clientInfo);
	 			}
	 		break;
		 	case "isbookreservable": //expected message "object[] = {book,subscriberId}"
		 			user = clientInfo.getUser();
		 			if(!user.getType().equals(User.UserType.SUBSCRIBER)) {
		 				sendMessageToClient("error", "Cant check reservability, Not a subscriber", client, clientInfo);
		 				break;
		 			}
		 			try {
		 				object = (Object[])message.getMessage();
		 				book = (Book)object[0];
		 				subscriberId = (int)object[1];
		 				result = ReserveController.IsBookReservable(dbConnection.getConnection(), book.getSerial_id(), subscriberId);
		 				if(result==-1) {
		 					throw new Exception();
		 				}
		 				sendMessageToClient("isbookreservable","" + result,client,clientInfo);
		 			}catch(Exception e) {
		 				e.printStackTrace();
		 				System.err.println("Could not check if book is reservable");
		 				sendMessageToClient("error","Could not check if book is reservable", client, clientInfo);
		 			}
		 		break;
		 	case "reservebook": //expected message: object[] = {book, subscriberId}
			 		user = clientInfo.getUser();
		 			if(!user.getType().equals(User.UserType.SUBSCRIBER)) {
		 				sendMessageToClient("error", "Cant reserve book, Not a subscriber", client, clientInfo);
		 				break;
		 			}
		 			try {
		 				object = (Object[])message.getMessage();
		 				book = (Book)object[0];
		 				subscriberId = (int)object[1];
		 				result = ReserveController.ReserveBook(dbConnection.getConnection(), book.getSerial_id(), subscriberId);
		 				if(result==-1) {
		 					throw new Exception();
		 				}
		 				dh = new DetailedHistory(clientInfo.getUser(), ActionType.RESERVE, LocalDate.now(),  " reserved book " + book.getName() + " on " + DateUtil.DateToString(LocalDate.now()));
		 				DetailedHistoryController.RecordHistory(dbConnection.getConnection(), dh);
		 				sendMessageToClient("msg","Book " +book.getName()+ " has been reserved",client,clientInfo);
		 			}catch(Exception e) {
		 				e.printStackTrace();
		 				System.err.println("Could not reserve book");
		 				sendMessageToClient("error","Could not reserve book", client, clientInfo);
		 			}
		 		break;
		 	case "encrypted":
		 		break;
		 	default:
		 			reply = new Message("error", clientInfo.getSessionId(), "Not a valid request, received: " + message.getRequest());
		 			handleMessageToClient(reply, client);
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
