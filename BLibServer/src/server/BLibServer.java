// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import controller.*;
import java.io.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
	  clientInfo.setUser(null);
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
   * Handles any messages received from the client by decrypting and analyzing
   * the request type, then taking the appropriate action. If a request type is
   * invalid or an error occurs, an error response is sent back.
   *
   * <p><strong>Supported request types include:</strong></p>
   * <ul>
   *   <li><b>"connect"</b> — Initializes the connection with a unique session ID 
   *       and notifies the client of successful connection.</li>
   *   <li><b>"getsubscribers"</b> — Retrieves a list of subscribers filtered by a certain element/value.</li>
   *   <li><b>"updatesubscriber"</b> — Updates an existing subscriber's information; 
   *       may trigger freeze/unfreeze notifications and history logs if changed by a librarian.</li>
   *   <li><b>"getsubscriber"</b> — Retrieves a single subscriber by ID.</li>
   *   <li><b>"registersubscriber"</b> — Registers a new subscriber using username, password, name, email, and phone.</li>
   *   <li><b>"logout"</b> — Logs out the current user from the server.</li>
   *   <li><b>"login"</b> — Authenticates a user (or sets them as GUEST) with a username and password.</li>
   *   <li><b>"getbook"</b> — Retrieves a single book by its ID.</li>
   *   <li><b>"getbooks"</b> — Retrieves a list of books filtered by a certain element/value.</li>
   *   <li><b>"getreservedbooks"</b> — Retrieves a list of books reserved by a specific subscriber.</li>
   *   <li><b>"borrowedbooks"</b> — Retrieves a list of borrowed books for a given subscriber.</li>
   *   <li><b>"borrowbook"</b> — Lends a book to a subscriber if permitted (librarian only, subscriber not frozen, etc.).</li>
   *   <li><b>"returnbook"</b> — Returns a borrowed book, updates relevant history, and notifies reserved subscribers.</li>
   *   <li><b>"gethistory"</b> — Retrieves a detailed history (actions taken) for a specific ID.</li>
   *   <li><b>"addhistory"</b> — Adds a new detailed history record for a specific user (librarian-only action).</li>
   *   <li><b>"bookinfo"</b> — Checks availability of a specific book via its serial ID.</li>
   *   <li><b>"booksinfo"</b> — Checks availability for a list of books.</li>
   *   <li><b>"isbookreservable"</b> — Checks if a book is reservable by a given subscriber (subscriber-only action).</li>
   *   <li><b>"reservebook"</b> — Reserves a book for a subscriber, updates notifications and history if successful.</li>
   *   <li><b>"isbookextendable"</b> — Checks if a borrowed book can be extended.</li>
   *   <li><b>"arebooksextendable"</b> — Checks if multiple borrowed books are extendable.</li>
   *   <li><b>"extendbook"</b> — Extends the return date of a borrowed book; updates history and notifies accordingly.</li>
   *   <li><b>"getnotifications"</b> — Retrieves notification records for a specific subscriber.</li>
   *   <li><b>"loanreport"</b> — Generates a report of average loan times filtered by year/month.</li>
   *   <li><b>"statusreport"</b> — Generates a report of subscriber statuses.</li>
   *   <li><b>"encrypted"</b> — (No operation performed; indicates data already encrypted.)</li>
   *   <li><b>"default"</b> — If the request is unrecognized, an error message is returned to the client.</li>
   * </ul>
   *
   * @param msg    the raw message from the client (commonly a {@code Message} object)
   * @param client the originating client connection
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
		 	
		 	case "getsubscribers": //expected message : String[2] = {element, value}
		 		try {
		 			String[] array = (String[])message.getMessage();
		 			String element = array[0];
		 			String value = array[1];
			 		List<Subscriber> subscriberList = SubscriberController.getSubscribersByElement(dbConnection.getConnection(), element, value);
			 		sendMessageToClient("subscribers", subscriberList, client, clientInfo);
		 		}
		 		catch(Exception e) {
		 			e.printStackTrace();
		 			System.err.println("Could not get subscribers by element");
		 			sendMessageToClient("error", "Could not get subscribers list from server", client, clientInfo);
		 		}
		 		break;
		 		
		 	case "updatesubscriber":
		 		//Update subscriber in DB sent from client in string form
		 		try {
		 			subscriber = Subscriber.subscriberFromString((String)message.getMessage());
			 		Subscriber oldSubscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriber.getSubscriberId());
			 		
			 		boolean success = SubscriberController.updateSubscriberInfo(dbConnection.getConnection(), subscriber);
			 		//If frozen was toggled by Librarian
			 		if(success && oldSubscriber.isFrozen() != subscriber.isFrozen() && clientInfo.getUser().getType().equals(User.UserType.LIBRARIAN)) { //has to be a librarian
			 			//If frozen status changed
			 			str = "Account id "+ subscriber.getSubscriberId() +" was set to ";
			 			if(subscriber.isFrozen()) {
			 				str += "FROZEN ";
			 			}
			 			else {
			 				str += "ACTIVE ";
			 			}
			 			str += "by " +UserController.getNameFromUser(dbConnection.getConnection(), clientInfo.getUser());
			 			str += " on " + LocalDate.now();
		 				Notification n = new Notification(subscriber, LocalDate.now(), str);
			 			NotificationController.Notify(dbConnection.getConnection(), n);
			 			dh = new DetailedHistory(
			 					UserController.getUserById(dbConnection.getConnection(), subscriber.getSubscriberId()),
			 					(subscriber.isFrozen()) ? DetailedHistory.ActionType.FREEZE : DetailedHistory.ActionType.UNFREEZE,
			 					LocalDate.now(),
			 					str);
			 			DetailedHistoryController.RecordHistory(dbConnection.getConnection(), dh);
			 		}
			 		sendMessageToClient("msg", "Subscriber updated successfully", client, clientInfo);
		 		}
		 		catch(Exception e) {
		 			System.err.println("Could not update subscriber");
		 			sendMessageToClient("error", "Could not update subscriber", client, clientInfo);
		 		}

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
		 		reply = new Message("getsubscriber", clientInfo.getSessionId(), subscriber);
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
				 			//Check user is not currently logged in
				 			boolean userConnected = false;
				 			for(ConnectionToClientInfo info : clientConnections) {
				 				//Username equal and already connected
				 				if(info.getUser() != null && 
				 					user.getUsername().contentEquals(info.getUser().getUsername())
				 					&& info.getStatus().equals(ClientConnectionStatus.Connected)) {
						 					userConnected = true;
						 					break;
				 				}
				 			}
				 			if(userConnected) {
				 				sendMessageToClient("error", "Username already connected to server.", client, clientInfo);
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
		 		
		 	case "getbooks": // get books by element and value, expected String[2] = {element, value}
	 			try {
	 				String element = ((String[])message.getMessage())[0];
	 				String value = ((String[])message.getMessage())[1];
		 			List<Book> books = BookController.GetBooksByElement(dbConnection.getConnection(), element, value);
		 			sendMessageToClient("books", books, client, clientInfo);
	 			}
	 			catch(Exception e) {
	 				reply = new Message("error", clientInfo.getSessionId(), "Could not parse not correct element and value for books search");
	 				handleMessageToClient(reply, client);
	 			}
		 		break;
		 	case "getreservedbooks": //expected message : userId
		 		try {
	 				Integer userId = (Integer)message.getMessage();
		 			List<Book> books = ReserveController.GetReservedBookBySubscriber(dbConnection.getConnection(), userId);
		 			if(books == null) {
		 				sendMessageToClient("error", "Could not get reserved books for subscriber " + userId, client, clientInfo);
		 			}
		 			sendMessageToClient("books", books, client, clientInfo);
	 			}
	 			catch(Exception e) {
	 				reply = new Message("error", clientInfo.getSessionId(), "Could not parse not correct element and value for books search");
	 				handleMessageToClient(reply, client);
	 			}
		 		break;
		 	case "borrowedbooks": //expected to get message of "subscriberId"
		 			try {
		 				int subscriber_id = (Integer)message.getMessage();
		 				List<BorrowedBook> borrowedBookList = LendController.GetBorrowedBooksBySubscriberId(dbConnection.getConnection(), subscriber_id);
		 				if(borrowedBookList == null) {
		 					throw new Exception();
		 				}
		 				sendMessageToClient("borrowedbooks", borrowedBookList, client, clientInfo);
		 			}
		 			catch(Exception e) {
		 				str = "Could not retreive borrowed books by subscriber";
		 				System.err.println(str);
		 				sendMessageToClient("error", str, client, clientInfo);
		 			}
		 		break;
		 	case "borrowbook": //expected to get message of Object[] =  {bookId, subscriberId, (LocalDate)borrow, (LocalDate)return}
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
		 				try {
		 					//Parse String
			 				object = (Object[])message.getMessage();
			 				int bookId = (Integer)object[0];
			 				subscriberId = (Integer)object[1];
			 				LocalDate dateFrom = (LocalDate)object[2];
			 				LocalDate dateTo = (LocalDate)object[3];
			 				
			 				//Make sure subscriber not frozen
			 				subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriberId);
			 				if(subscriber.isFrozen()) {
			 					reply = new Message("error", clientInfo.getSessionId(), "Access Denied, Subscriber account frozen");
			 					handleMessageToClient(reply, client);
			 					break;
			 				}
			 				//Check if book is reserved by another subscriber
			 				subscriber = ReserveController.GetSubscriberThatReservedBook(dbConnection.getConnection(), bookId);
			 				if(subscriber != null && subscriber.getSubscriberId() != subscriberId) {
			 					sendMessageToClient("error", "Book is already reserved by another user.", client, clientInfo);
			 					break;
			 				}
			 				result = ReserveController.RemoveReservation(dbConnection.getConnection(), bookId, subscriberId);
			 				if(result<=0) {
			 					System.err.println("Could not remove reservation during borrow");
			 				}
			 				//Lend based on serial or id
			 				result = LendController.LendBookId(dbConnection.getConnection(), subscriberId, dateFrom, dateTo, bookId);
			 				if(result<=0) {
			 					reply = new Message("error", clientInfo.getSessionId(), "Could not borrow book, it might be taken");
			 					handleMessageToClient(reply, client);
			 					break;
			 				}
			 				reply = new Message("msg", clientInfo.getSessionId(), "Book " + bookId + " Lent to Subscriber " + subscriberId + " successfully.");
			 				handleMessageToClient(reply, client);
			 				break;
		 				}
		 				catch(Exception e) {
		 					reply = new Message("error", clientInfo.getSessionId(), "Could not borrow book");
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
			 			bb = LendController.GetBorrowedBookByBookId(dbConnection.getConnection(), book.getId());
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
			 			//Report Return to Database
			 			int loanTime = (int)ChronoUnit.DAYS.between(bb.getBorrowedDate(), date);
			 			ReportController.AddReportToDatabase(dbConnection.getConnection(), book, loanTime);
			 			
			 			reply = new Message("msg", clientInfo.getSessionId(), replyStr);
			 			handleMessageToClient(reply, client);
			 			//notify reserved
			 			subscriber = ReserveController.GetSubscriberThatReservedBook(dbConnection.getConnection(), book.getId());
			 			if(subscriber != null) {
			 				Notification n = new Notification(subscriber , LocalDate.now(), "Book "+book.getName()+ " has been returned and become Available");
			 				NotificationController.Notify(dbConnection.getConnection(), n);
			 				NotificationController.SendSmsNotification(n.getDescription(), n.getSubscriber());
			 				NotificationController.SendEmailNotification(n.getDescription(), n.getSubscriber());
			 			}
		 			}
		 			catch(Exception e) {
		 				System.err.println("Failed to parse returnbook message");
		 				reply = new Message("error", clientInfo.getSessionId(), "Failed to parse message to return book");
		 				handleMessageToClient(reply, client);
		 			}
	 			break;
		 	case "gethistory": //expected message "historyId"
		 			str = (String)message.getMessage();
		 			try {
		 				int historyId = Integer.parseInt(str);
		 				List<DetailedHistory> historyList = DetailedHistoryController.GetHistoryListFromDatabase(dbConnection.getConnection(), historyId);
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
		 	case "addhistory": //expected message Object[] = {(DetailedHistory w/out user), (Integer)historyId, (Integer)userId}
			 		if(!clientInfo.getUser().getType().equals(User.UserType.LIBRARIAN)) {
		 				reply = new Message("error", clientInfo.getSessionId(), "Access denied, not a librarian.");
		 				handleMessageToClient(reply, client);
		 				break;
		 			}
		 			try {
		 				object = (Object[])message.getMessage();
				 		dh = (DetailedHistory)object[0];
		 				Integer historyId = (Integer)object[1];
		 				Integer userId = (Integer)object[2];
		 				user = UserController.getUserById(dbConnection.getConnection(), userId);
		 				dh.setUser(user);
		 				DetailedHistoryController.RecordHistory(dbConnection.getConnection(), dh);
		 				sendMessageToClient("msg", "added history to user " + dh.getUser().getId(), client, clientInfo);

		 			}
		 			catch(Exception e) {
		 				System.err.println("Could not parse message in add history");
		 				reply = new Message("error", clientInfo.getSessionId(), "Could not add history using historyId");
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
		 				subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriberId);
		 				if(subscriber.isFrozen()) {
		 					sendMessageToClient("error", "Can't reserve book, Account is frozen.", client, clientInfo);
		 					break;
		 				}
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
		 	case "isbookextendable": //expected message: "bookId"
		 			int bookId;
		 			try {
		 				bookId = Integer.parseInt((String)message.getMessage());
		 				result = ExtendController.IsBookExtendable(dbConnection.getConnection(), bookId);
		 				if(result<0)
		 					throw new Exception();
		 				else if(result==0)
		 					sendMessageToClient("data",false,client,clientInfo);
		 				else
		 					sendMessageToClient("data",true,client,clientInfo);
		 			}
		 			catch(Exception e) {
		 				e.printStackTrace();
		 				str = "Could not check if book is extendable";
		 				sendMessageToClient("error", str, client, clientInfo);
		 				System.err.println(str);
		 			}
		 		break;
		 	case "arebooksextendable": //expected message: "List<Integer> bookId"
	 			try {
	 				List<Integer> bookIds = (List<Integer>)message.getMessage();
	 				if(bookIds == null)
	 					throw new Exception();
	 				List<Boolean> extendableList = new  ArrayList<Boolean>();
	 				for(int id : bookIds) {
		 				result = ExtendController.IsBookExtendable(dbConnection.getConnection(), id);
		 				extendableList.add((result>0));
	 				}
	 				sendMessageToClient("data", extendableList, client, clientInfo);
	 			}
	 			catch(Exception e) {
	 				e.printStackTrace();
	 				str = "Could not check if books in list are extendable";
	 				sendMessageToClient("error", str, client, clientInfo);
	 				System.err.println(str);
	 			}
	 		break;
		 	case "extendbook": //expected message "int[2] = {bookId, amountOfDays}"
		 			try {
		 				int[] values = (int[])message.getMessage();
		 				int book_id = values[0];
		 				int amountOfDays = values[1];
		 				user = clientInfo.getUser();
		 				str = UserController.getNameFromUser(dbConnection.getConnection(), user);
		 				bb = LendController.GetBorrowedBookByBookId(dbConnection.getConnection(), book_id);
		 				subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), bb.getBorrowingSubscriber().getSubscriberId());
		 				if(subscriber.isFrozen()) {
		 					sendMessageToClient("error", "Can't extend book, account is frozen.", client, clientInfo);
		 					break;
		 				}
		 				//Extend Book by amountOfDays

		 				LocalDate newDate = ExtendController.ExtendBookReturnDate(dbConnection.getConnection(), book_id, amountOfDays);
		 				String newDateStr = DateUtil.DateToString(LocalDate.now().plusDays(amountOfDays));
		 				if(newDate == null) {
		 					sendMessageToClient("error", "Could not extend Book to " + newDateStr, client, clientInfo);
		 					break;
		 				}
		 				//Notify client and record action
		 				sendMessageToClient("msg", "Book's return date was extended by " + amountOfDays +" days", client, clientInfo);
		 				book = BookController.GetBookById(dbConnection.getConnection(), book_id);
		 				dh = new DetailedHistory(UserController.getUserById(dbConnection.getConnection(), subscriber.getSubscriberId()), ActionType.EXTEND, LocalDate.now(), subscriber.getSubscriberName() + "'s book " + "\""+book.getName()+"\" was extended to date "+ newDateStr + (user.getType().equals(User.UserType.LIBRARIAN) ? " by librarian "+str:""));
		 				int success = DetailedHistoryController.RecordHistory(dbConnection.getConnection(), dh);
		 				if(success <= 0) {
		 					System.err.println("Could not record history for extendbook");
		 				}
		 			}
		 			catch(Exception e) {
		 				str = "Could not parse info for extendbook";
		 				System.err.println(str);
		 				sendMessageToClient("error", str, client, clientInfo);
		 			}
		 		break;
		 	case "getnotifications": //expected message "(Integer)subscriberId"
		 		try {
		 			subscriberId = (Integer)message.getMessage();
		 			subscriber = SubscriberController.getSubscriberById(dbConnection.getConnection(), subscriberId);
		 			if(subscriber == null)
		 				throw new Exception();
		 			List<Notification> notificationList = NotificationController.GetNotificationsFromDatabase(dbConnection.getConnection(), subscriber.getNotificationHistory());
		 			if(notificationList == null)
		 				notificationList = new ArrayList<Notification>();
		 			
		 			sendMessageToClient("notifications", notificationList, client, clientInfo);
		 			
		 		}
		 		catch(Exception e) {
		 			e.printStackTrace();
		 			System.err.println("Could not get notifications for subscriber");
		 		}
		 		break;
		 	case "loanreport": //expected message
		 			try {
		 				object = (Object[])message.getMessage();
		 				int year = (Integer)object[0];
		 				int month = (Integer)object[1];
		 				Object[] objectReport = ReportController.GenerateLoanTimeReport(dbConnection.getConnection(), year, month);
		 				sendMessageToClient("data", objectReport, client, clientInfo);
		 			}catch(Exception e) {
		 				sendMessageToClient("error", "Could not generate loan report", client, clientInfo);
		 			}
		 		break;
		 	case "statusreport": //expected message
	 			try {
	 				object = (Object[])message.getMessage();
	 				Object[] objectReport = ReportController.GenerateSubscriberReport(dbConnection.getConnection());
	 				sendMessageToClient("data", objectReport, client, clientInfo);
	 			}catch(Exception e) {
	 				sendMessageToClient("error", "Could not generate loan report", client, clientInfo);
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
