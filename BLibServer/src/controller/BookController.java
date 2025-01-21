package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import common.Book;
import common.Subscriber;

public class BookController {
	/**
	 * Get Book from Database using ID
	 * @param connection
	 * @param bookId
	 * @return Book
	 */
	public static Book GetBookById(Connection connection, int bookId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM book WHERE book_id = ?");
			pstmt.setInt(1, bookId);
			ResultSet rs = pstmt.executeQuery();
			Book book = null;
			if(rs.next()) {
				int book_id = rs.getInt("book_id");
				int book_serial_id = rs.getInt("book_serial_id");
				String book_name = rs.getString("book_name");
				String book_author = rs.getString("book_author");
				String book_description = rs.getString("book_description");
				String book_genre = rs.getString("book_genre");
				String book_location = rs.getString("book_location");
				book = new Book(book_serial_id, book_name, book_author, book_description, book_genre, book_location);
				book.setId(book_id);
			}
			return book;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Get Book from Database (ID)");
			return null;
		}
	}

	/**
	 * Private Generic get books by a certain element.
	 * @param connection
	 * @param element
	 * @param value
	 * @return List of Books
	 */
	public static List<Book> GetBooksByElement(Connection connection, String element, String value){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		int integerValue;
		boolean valueIsInteger = false;
		PreparedStatement pstmt = null;
		try {
			integerValue = Integer.parseInt(value);
			valueIsInteger = true;
		}
		catch(Exception e) {
			valueIsInteger = false;
		}
		try {
			//Incase value is of type Int
			if(valueIsInteger) {
				pstmt = connection.prepareStatement("SELECT * FROM book WHERE " + element + " = ?");
				pstmt.setInt(1, Integer.parseInt(value));
			}
			else {
				pstmt = connection.prepareStatement("SELECT * FROM book WHERE " + element + " LIKE ?");
				pstmt.setString(1, "%" + value + "%");
			}
			ResultSet rs = pstmt.executeQuery();
			List<Book> books = new ArrayList<Book>();
			while(rs.next()) {
				try {
					Book book = null;
					int book_id = rs.getInt("book_id");
					int book_serial_id = rs.getInt("book_serial_id");
					String book_name = rs.getString("book_name");
					String book_author = rs.getString("book_author");
					String book_description = rs.getString("book_description");
					String book_genre = rs.getString("book_genre");
					String book_location = rs.getString("book_location");
					book = new Book(book_serial_id, book_name, book_author, book_description, book_genre, book_location);
					book.setId(book_id);
					books.add(book);
				}
				catch(Exception e){
					e.printStackTrace();
					System.err.println("Could not add a book to list using element "+element+" with value "+value+".");
				}
			}
			return books;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Get Book from Database (ID)");
			return null;
		}
	}
	
	/**
	 * Get Books from Database using Name
	 * @param connection
	 * @param bookName
	 * @return List of Books
	 */
	public static List<Book> GetBooksByName(Connection connection, String name) {
		return GetBooksByElement(connection, "book_name", name);
	}

	/**
	 * Get Books from Database using Name
	 * @param connection
	 * @param bookDescription
	 * @return List of Books
	 */
	public static List<Book> GetBooksByDescription(Connection connection, String description) {
		return GetBooksByElement(connection, "book_description", description);
	}
	
	/**
	 * Get Books from Database using Genre
	 * @param connection
	 * @param bookGenre
	 * @return List of Books
	 */
	public static List<Book> GetBooksByGenre(Connection connection, String genre) {
		return GetBooksByElement(connection, "book_genre", genre);
	}
	
	/**
	 * Get Books from Database using specific SerialId
	 * @param connection
	 * @param bookSerialId
	 * @return List of Books
	 */
	public static List<Book> GetBooksBySerialId(Connection connection, int serialId) {
		return GetBooksByElement(connection, "book_serial_id", ""+serialId);
	}
	
	/**
	 * Checks how many copies of a book are available
	 * @param connection
	 * @param bookSerialId
	 * @return number of available books
	 */
	public static int CheckBookAvailability(Connection connection, int bookSerialId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		try {
			String query = "SELECT COUNT(*) AS available_books FROM book b WHERE b.book_serial_id = ? AND b.book_id NOT IN (SELECT bb.book_id FROM borrowed_book bb)";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, bookSerialId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt("available_books");
			}
			return 0;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not check how many books are available");
			return -1;
		}
	}

	/**
	 * Get Closest Return Date for a book serial Id
	 * @param connection
	 * @param bookSerialId
	 * @return Date of Closest Return, null if not fail or not relevant
	 */
	public static Date ClosestReturnDate(Connection connection, int bookSerialId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			String query = "SELECT return_date_id FROM borrowed_book WHERE book_id IN (SELECT book_id FROM book WHERE book_serial_id = ?)";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, bookSerialId);
			ResultSet rs = pstmt.executeQuery();
			Date closestDate = null;
			while(rs.next()) {
				int return_date_id = rs.getInt("return_date_id");
				Date returnDate = DateController.GetDateById(connection, return_date_id);
				if(closestDate == null || returnDate.before(closestDate)) {
					closestDate = returnDate;
				}
			}
			return closestDate;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not check how many books are available");
			return null;
		}
	}
	
	/**
	 * Add an order for a book to a subscriber in database
	 * @param connection
	 * @param book
	 * @param subscriber
	 * @return -1=error, 0=fail, 1=success
	 */
	public static int OrderBookForSubscriber(Connection connection, Book book, Subscriber subscriber) {
		if(connection == null) {
			System.err.println("Could not connect to Database.");
			return -1;
		}
		
		try {
			//Check/Get Current Date
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO order(book_id, subscriber_id, order_date_id) VALUES(?,?,?)");
			LocalDate currentLocalDate = LocalDate.now();
			Date currentDate = new Date(currentLocalDate.getYear(), currentLocalDate.getMonthValue(), currentLocalDate.getDayOfMonth());
			//Get Date_id from current date
			int dateId = DateController.GetDateIdByDate(connection, currentDate);
			if(dateId <= 0) {
				int dateSuccess;
				//Create date id from current date
				dateSuccess = DateController.CreateDateIdByDate(connection, currentDate);
				if(dateSuccess <= 0)
					return -1; //failed to create
				dateId = DateController.GetDateIdByDate(connection, currentDate);
				if(dateId <= 0)
					return -1; //failed to reaquire post creation
			}
			pstmt.setInt(1, book.getId());
			pstmt.setInt(2, subscriber.getSubscriberId());
			pstmt.setInt(3, dateId);
			int success = pstmt.executeUpdate();
			return (success > 0) ? 1 : 0;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Order book for Subscriber");
			return -1;
		}
	}
}
