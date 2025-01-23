// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package client;
import java.io.*;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.List;

import client.*;
import common.*;


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
  
  /////////	Requests from Server	////////
  /**
   * Send request and message to server (avoid null overusage)
   * @param request
   * @param message
   */
  public void SendRequestToServer(String request, String message) {
	  accept(new Message(request, null, message));
  } 
  /**
   * Login to Server with Username and Password
   * @param username
   * @param password
   * @return Object[] with Object[0]=(User)User, Object[1]=(String)User's Full Name
   */
  public Object[] LoginToServer(String username, String password) {
	  SendRequestToServer(username, password);
	  Object[] values = {getClientUser(), getClientName()};
	  return values;
  }
  
  /**
   * Request Server for Subscriber
   * @param subscriber_id
   * @return Subscriber
   */
  public Subscriber requestServerForSubscriber(String id){
	  SendRequestToServer("getsubscriber", id);
	  return getClientSubscriber();
  }
  
  /**
   * Request Server for Subscriber List of all Subscribers
   * @return Subscriber List
   */
  public List<Subscriber> requestServerForSubscriberList(){
	  SendRequestToServer("getsubscribers", null);
	  return getClientSubscriberList();
  }
  
  /**
   * Request Server to Update a Subscriber
   * @param updated subscriber with same id
   */
  public void requestServerToUpdateSubscriber(Subscriber subscriber) {
	  SendRequestToServer("updatesubscriber", subscriber.toString());
  }
  
  /**
   * Request Server for Book
   * @param book_id
   * @return Book
   */
  public Book requestBookFromServer(int book_id){
	  SendRequestToServer("getbook", ""+book_id);
	  return getClientBook();
  }
  
  /**
   * Request Server to Search For Books Based on Element
   * e.g. Searching for Books by name will be 
   * requestServerSearchForBooks("book_name", "Harry Potter")
   * will return all books with the name "Harry Potter"
   * @param element: book_name, book_genre, book_description
   * @param value of element
   * @return List of Books
   */
  public List<Book> requestServerSearchForBooks(String element, String value){
	  SendRequestToServer("getbooks", element+";"+value);
	  return getClientBookList();
  }
  
  /**
   * Request Server to Borrow Book
   * e.g. Borrow Book using book's serial id
   * requestServerToBorrowBook("bookSerialId", "subscriberId", dateBorrow, dateReturn, "serial")
   * e.g. Borrow Book using book's id
   * requestServerToBorrowBook("bookId", ..., ..., ..., ..., "id")
   * check LastResponse for result
   * @param bookIdOrSerial book id or serial, depends on BookIdType
   * @param subscriberId
   * @param borrowDate
   * @param returnDate
   * @param bookIdType "id" for book_id or "serial" for bookSerialId
   */
  public void requestServerToBorrowBook(String bookIdOrSerial, String subscriberId, LocalDate borrowDate, LocalDate returnDate, String bookIdType) {
	  SendRequestToServer("borrowbook", bookIdOrSerial+";"+subscriberId+";"+DateUtil.DateToString(borrowDate)+";"+DateUtil.DateToString(returnDate)+";"+bookIdType);
  }
  
  /**
   * Request Server to Return book
   * Return Date set as time of method activation
   * @param book
   * @param subscriber
   */
  public void requestServerToReturnBook(Book book, Subscriber subscriber) {
	  SendRequestToServer("returnbook", book+";"+subscriber+";"+DateUtil.DateToString(LocalDate.now()));
  }
  
  /**
   * Request Server for Detailed History List for a specific User
   * Can be found in any subscriber/librarian
   * @param userId (subscriberId or librarianId)
   * @return
   */
  public List<DetailedHistory> requestServerForHistoryList(int userId){
	  SendRequestToServer("gethistory", ""+userId);
	  return client.historyList;
  }
  
  ////////	Get Client Local Variables	/////////
  /**
   * Get Last Responses from Server to Client
   * if LastResponse = error, LastResponseError will have str value
   * if LastResponse = msg, LastResponseMsg will have str value
   * @return String[3] with {LastResponse, LastResponseMsg, LastResponseError}
   */
  public String[] getClientLastResponses() {
	  String[] responses = {client.lastResponse, client.lastResponseMsg, client.lastResponseError};
	  return responses;
  }
  
  /**
   * Get Client's User Full Name
   * @return String
   */
  public String getClientName() {
	  return client.name;
  }
  
  /**
   * Get Client's Last Returned User
   * @return User
   */
  public User getClientUser() {
	  return client.user;
  }
  
  /**
   * Get Client's Last Returned Subscriber List
   * @return Subscriber List
   */
  public List<Subscriber> getClientSubscriberList() {
	  return client.getSubscriberList();
  }
  
  /**
   * Get Client's Last Returned Subscriber
   * @return Subscriber
   */
  public Subscriber getClientSubscriber() {
	  return client.subscriber;
  }
  
  /**
   * Get Client's Last Returned Book LIst
   * @return Book List
   */
  public List<Book> getClientBookList(){
	  return client.books;
  }
  
	/**
	 * Get Client's Last Returned Book
	 * @return book
	 */
  public Book getClientBook() {
	  return client.book;
  }
  
  /**
   * Get Client's Last Returned DetailedHistory
   * @return DetailedHistory
   */
  public DetailedHistory getClientHistory() {
	  return client.history;
  }
  
  /**
   * Get Client's Last Returned DetailedHistory List
   * @return DetailedHistory List
   */
  public List<DetailedHistory> getClientHistoryList() {
	  return client.historyList;
  }
}
//End of ConsoleChat class
