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
  public void SendRequestToServer(String request, Object message) {
	  accept(new Message(request, null, message));
  } 
  /**
   * Login to Server with Username and Password
   * @param username
   * @param password
   * @return Object[] with Object[0]=(User)User, Object[1]=(String)User's Full Name
   */
  public Object[] LoginToServer(String username, String password) {
	  if(username.toUpperCase().contentEquals("GUEST")) {
		  SendRequestToServer("login", "GUEST");
	  }
	  else {
		  SendRequestToServer("login", username+" "+password);  
	  }
	  Object[] values = {getClientUser(), getClientName()};
	  return values;
  }
  
  /**
   * Request server to Log out, removes user in fullName info from client
   */
  public void LogoutFromServer() {
	  SendRequestToServer("logout", "");
	  ClientUI.chat.client.user = null;
	  ClientUI.chat.client.name = null;
  }
  
  /**
   * Request Server for Subscriber
   * @param subscriber_id
   * @return Subscriber
   */
  public Subscriber requestServerForSubscriber(String id){
	  SendRequestToServer("getsubscriber", id);
	  return client.subscriber;
  }
  
  /**
   * Request Server for Subscriber List of all Subscribers
   * @return Subscriber List
   */
  public List<Subscriber> requestServerForSubscriberList(String element, String value){
	  SendRequestToServer("getsubscribers", new String[] {element, value});
	  return client.subscriberList;
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
	  SendRequestToServer("getbooks", new String[] {element,value});
	  return getClientBookList();
  }
  
  /**
   * Request Server to Return Availiblity+ClosestReturnDate for books in bookList
   * @param bookList
   * @return Object[2] with Object[0]=List of availibility<p> Object[1]=List of Closest Return Date
   */
  public Object[] requestServerForBookListAvailibilityInfo(List<Book> bookList) {
	  SendRequestToServer("booksinfo", bookList);
	  return getClientBookAvailibilityInfo();
  }
  
  /**
   * Request Server to Return Availiblity+ClosestReturnDate for books in bookList
   * @param bookList
   * @return
   */
  public Object[] requestServerForBookAvailibilityInfo(Book book) {
	  SendRequestToServer("booksinfo", book.toString());
	  return getClientBookAvailibilityInfo();
  }
  
  /**
   * Request Server to check for books in a list whether they are extendable
   * @param bookIdList
   * @return Object data of Boolean List, entry for each book in the same order
   */
  public List<Boolean> requestServerForBookListExtendability(List<Integer> bookIdList) {
	  SendRequestToServer("arebooksextendable", (Object)bookIdList);
	  return (List<Boolean>)client.data;
  }
  
  /**
   * Request Server to check whether a book is extendable
   * @param bookId
   * @return Object data of Boolean (true/false)
   */
  public Boolean requestServerForBookExtendability(int bookId) {
	  SendRequestToServer("isbookextendable", (Object)bookId);
	  return (Boolean)client.data;
  }
  
  /**
   * Request Server to Extend a book By certain amount of days
   * @param bookId
   * @param amountOfDays
   * @return 0=fail/error, 1=success
   */
  public int requestServerToExtendBookReturnDate(int bookId, int amountOfDays) {
	  SendRequestToServer("extendbook", (new int[] {bookId, amountOfDays}));
	  String lr[] = getClientLastResponses();
	  if(lr[0].contentEquals("error"))
		  return 0;
	  else
		  return 1;
  }
  
  /**
   * Request Server to Register Subscriber
   * @param username
   * @param password
   * @param name
   * @param email
   * @param phone
   * @return boolean of success
   */
  public boolean requestServerToRegisterSubscriber(String username, String password, String name, String email, String phone) {
	  SendRequestToServer("registersubscriber", username+";"+password+";"+name+";"+email+";"+phone);
	  if(client.lastResponse.contentEquals("msg")) {
		  return true;
	  }
	  else {
		  return false;
	  }
	  
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
  public int requestServerToBorrowBook(String bookIdOrSerial, String subscriberId, LocalDate borrowDate, LocalDate returnDate, String bookIdType) {
	  SendRequestToServer("borrowbook", bookIdOrSerial+";"+subscriberId+";"+DateUtil.DateToString(borrowDate)+";"+DateUtil.DateToString(returnDate)+";"+bookIdType);
	  if(client.lastResponse.contentEquals("msg")) {
		  return 1;
	  }
	  else {
		  return 0;
	  }
  }
  
  /**
   * Request client for borrowed books list by subscriber
   * @param subscriberId
   * @return List of BorrowedBooks
   */
  public List<BorrowedBook> requestServerForBorrowedBooksBySubscriber(int subscriberId){
	  SendRequestToServer("borrowedbooks", (Integer)subscriberId);
	  return client.borrowedBooks;
  }
  
  
  /**
   * Request Server to Return book
   * Return Date set as time of method activation
   * @param bookId
   * @param subscriber
   */
  public void requestServerToReturnBook(String bookId) {
	  SendRequestToServer("returnbook", bookId+";"+DateUtil.DateToString(LocalDate.now()));
  }
  
  /**
   * Request Server for Detailed History List for a specific User
   * Can be found in any subscriber/librarian
   * @param historyId (subscriberId or librarianId)
   * @return
   */
  public List<DetailedHistory> requestServerForHistoryList(int userId){
	  SendRequestToServer("gethistory", ""+userId);
	  return client.historyList;
  }
  
  /**
   * Request server to add a history into a history list
   * (subscribers have historyId btw) 
   * @param DetailedHistory (user can be null)
   * @param historyId int
   * @return boolean success/fail
   */
  public boolean requestServerToAddHistoryToSubscriber(DetailedHistory dh, int historyId, int userId){
	  SendRequestToServer("addhistory", new Object[] {dh, (Integer)historyId, (Integer)userId});
	  String[] lr = ClientUI.chat.getClientLastResponses();
	  if(lr[0].contentEquals("error")) {
		  return false;
	  }
	  return true;
  }
  
  
  /**
   * Request Server for Notifications for a specific subscriber
   * @param subscriberId
   * @return List<Notification>
   */
  public List<Notification> requestServerForNotifications(int subscriberId){
	  SendRequestToServer("getnotifications", (Integer)subscriberId);
	  return client.notifications;
  }
  
  /**
   * Request Server to check whether a book is reservable
   * @param book
   * @param subscriber
   * @return int 0=nope, 1=yep, 2=already reserved by subscriber
   */
  public int requestServerWhetherBookIsReservable(Book book, int subscriberId) {
	  Object[] object = new Object[] {book, subscriberId};
	  SendRequestToServer("isbookreservable", object);
	  return client.intResponse;
  }
  
  /**
   * Requests Server to a Reserve Book
   * @param book
   * @param subscriber
   * @return int 0=fail, 1=success
   */
  public int requestServerToReserveBook(Book book, int subscriberId) {
	  Object[] object = new Object[] {book, subscriberId};
	  SendRequestToServer("reservebook", object);
	  if(client.lastResponse.contentEquals("msg")) {
		  return 1;
	  }
	  else {
		  return 0;
	  }
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
	  return client.subscriberList;
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
   * Get Client's Last Book Availibility Info
   * @return Object[] with object[0]= boolean/booleans List, object[1]=return date/dates List
   */
  public Object[] getClientBookAvailibilityInfo() {
	  return client.bookAvailibilityInfo;
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
