package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import common.*;

public class ReserveController {
	
	/**
	 * Checks if a book is already reserved by a subscriber
	 * @param connection
	 * @param bookSerialId
	 * @param subscriberId
	 * @return int {@code -1=error, 0=nope, 1=yep}
	 */
	public static int IsBookReservedBySubscriber(Connection connection, int bookSerialId, int subscriberId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return -1;
		}
		
		try {
			String query = "SELECT COUNT(*) AS order_count " +
		               "FROM \"order\" o " +
		               "JOIN \"book\" b ON o.book_id = b.book_id " +
		               "WHERE o.subscriber_id = ? AND b.book_serial_id = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, subscriberId);
			pstmt.setInt(2, bookSerialId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				int count = rs.getInt("order_count");
				if(count > 0) {
					return 1;
				}
			}
			return 0;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not check if a book is reserved by subscriber");
			return -1;
		}
	};
	
	/**
	 * Returns how many of a book are already reserved
	 * @param connection
	 * @param bookSerialId
	 * @return int {@code -1=error, 0=all reserved, >0=number of books not reserved}
	 */
	public static int GetBookUnreservedCopies(Connection connection, int bookSerialId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return -1;
		}
		
		try {
	        String query = "SELECT COUNT(*) AS not_ordered_count " +
                    "FROM \"book\" b " +
                    "LEFT JOIN \"order\" o ON b.book_id = o.book_id " +
                    "WHERE b.book_serial_id = ? AND o.order_id IS NULL";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, bookSerialId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				int count = rs.getInt("not_ordered_count");
				return count;
			}
			return 0;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not check if a book is reserved by subscriber");
			return -1;
		}
	}
	
	/**
	 * Returns whether a book is reservable by a subscriber
	 * @param connection
	 * @param bookSerialId
	 * @param subscriberId
	 * @return int {@code -1=error, 0=nope, 1=yep, 2=already reserved}
	 */
	public static int IsBookReservable(Connection connection, int bookSerialId, int subscriberId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return -1;
		}
		
		try {
			//Check if the book is available
			int result = BookController.CheckBookSerialAvailability(connection, bookSerialId);
			if(result>0) { //Book is available
				return 0;
			}
			//Check if book is already reserved by Subscriber
			result = IsBookReservedBySubscriber(connection, bookSerialId, subscriberId);
			if(result>0) { //book already reserved
				return 2;
			}
			//Check if all books have already been reserved
			result = GetBookUnreservedCopies(connection, bookSerialId);
			if(result==0) {
				return 0;
			}
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not check if a book is reservable");
			return -1;
		}
	}
	
	/**
	 * Get Reserved books by subscriber
	 * @param connection
	 * @param subscriberId
	 * @return {@code List<Book>}
	 */
	public static List<Book> GetReservedBookBySubscriber(Connection connection, int subscriberId){
		if(connection == null) {
			System.err.println("Could not connect to database");
			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT book_id FROM `order` WHERE subscriber_id = ?");
			pstmt.setInt(1, subscriberId);
			ResultSet rs = pstmt.executeQuery();
			List<Book> books = new ArrayList<Book>();
			while(rs.next()) {
				int book_id = rs.getInt("book_id");
				Book book = BookController.GetBookById(connection, book_id);
				books.add(book);
			}
			return books;
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not get reserved books by subscriberId");
			return null;
		}
	}
	
	/**
	 * Returns the subscriber that reserved a bookId
	 * @param connection
	 * @param bookId
	 * @return Subscriber
	 */
	public static Subscriber GetSubscriberThatReservedBook(Connection connection, int bookId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT subscriber_id FROM `order` WHERE book_id = ?");
			pstmt.setInt(1, bookId);
			ResultSet rs = pstmt.executeQuery();
			int subscriberId = 0;
			if(rs.next()) {
				subscriberId = rs.getInt("subscriber_id");
			}
			if(subscriberId <= 0) {
				return null;
			}
			Subscriber subscriber = SubscriberController.getSubscriberById(connection, subscriberId);
			if(subscriber == null)
				throw new Exception();
			return subscriber;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not return subscriber who reserved a book");
			return null;
		}
	}
	
	/**
	 * Reserve a book
	 * @param connection
	 * @param bookSerialId
	 * @param subscriberId
	 * @return int {@code -1=error, 0=fail, 1=success}
	 */
	public static int ReserveBook(Connection connection, int bookSerialId, int subscriberId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return -1;
		}
		
		try {
			//Get book id's that are not ordered yet with serialid
			String query = "SELECT b.book_id, "
                    + "       d.date_id AS return_date " 
                    + "FROM borrowed_book b "
                    + "JOIN book bk ON b.book_id = bk.book_id "
                    + "JOIN \"date\" d ON b.return_date_id = d.date_id "
                    + "LEFT JOIN \"order\" o "
                    + "       ON b.book_id = o.book_id "
                    + "      AND b.subscriber_id = o.subscriber_id "
                    + "WHERE o.order_id IS NULL "
                    + "  AND bk.book_serial_id = ?;"; 
	        PreparedStatement pstmt = connection.prepareStatement(query);
	        pstmt.setInt(1, bookSerialId);
	        ResultSet rs = pstmt.executeQuery();
	        int bookId = 0;
	        LocalDate closestReturnDate = null;
	        while(rs.next()) { //get book_id of the closest return date
	        	LocalDate date = DateController.GetDateById(connection, rs.getInt("return_date"));
	        	if(closestReturnDate == null || date.isBefore(closestReturnDate)) {
		        	bookId = rs.getInt("book_id");
		        	closestReturnDate = date;
	        	}
	        }
	        LocalDate dateNow = LocalDate.now();
	        int dateId = DateController.GetOrCreateDateIdByDate(connection, dateNow);
	        //Reserve Book
			pstmt = connection.prepareStatement("INSERT INTO `order`(book_id, subscriber_id, order_date_id) VALUES (?,?,?)");
			pstmt.setInt(1, bookId);
			pstmt.setInt(2, subscriberId);
			pstmt.setInt(3, dateId);
			int result = pstmt.executeUpdate();
			if(result<=0)
				return 0;
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not check if a book is reservable");
			return -1;
		}
	}

	/**
	 * Removes Reservation from Database with bookId and subscriberId
	 * @param connection
	 * @param bookId
	 * @param subscriberId
	 * @return int {@code -1=error, 0=fail, 1=success}
	 */
	public static int RemoveReservation(Connection connection, int bookId, int subscriberId) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return -1;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("DELETE FROM `order` WHERE book_id = ? AND subscriber_id = ?");
			pstmt.setInt(1, bookId);
			pstmt.setInt(2, subscriberId);
			int success = pstmt.executeUpdate();
			if(success<=0) {
				return 0;
			}
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not remove reservation");
			return -1;
		}
	}
	
	/**
	 * Get all Reservations in Database
	 * @param connection
	 * @return
	 * {@code Object[0] = List<Book> reservedBooks}
	 * {@code Object[1] = List<Subscriber> subscribersThatReserved}
	 * {@code Object[2] = List<LocalDate> reservedDates}
	 */
	public static Object[] GetAllReservations(Connection connection) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM `order`");
			ResultSet rs = pstmt.executeQuery();
			List<Book> books = new ArrayList<Book>();
			List<Subscriber> subscribers = new ArrayList<Subscriber>();
			List<LocalDate> dates = new ArrayList<LocalDate>();
			while(rs.next()) {
				int bookId = rs.getInt("book_id");
				int subscriberId = rs.getInt("subscriber_id");
				int order_date_id = rs.getInt("order_date_id");
				Book book = BookController.GetBookById(connection, bookId);
				Subscriber subscriber = SubscriberController.getSubscriberById(connection, subscriberId);
				LocalDate date = DateController.GetDateById(connection, order_date_id);
				books.add(book);
				subscribers.add(subscriber);
				dates.add(date);
			}
			return new Object[] {books, subscribers, dates};
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not remove reservation");
			return null;
		}
	}
}
