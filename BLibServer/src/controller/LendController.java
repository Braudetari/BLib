package controller;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import common.*;
import common.DetailedHistory.ActionType;
import server.DatabaseConnection;

public class LendController {
	public static List<BorrowedBook> GetAllBorrowedBooks(Connection connection) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * from borrowed_book");
			ResultSet rs = pstmt.executeQuery();
			List<BorrowedBook> borrowedBooks = new ArrayList<BorrowedBook>();
			while(rs.next()) {
				int book_id = rs.getInt("book_id");
				int subscriber_id = rs.getInt("book_id");
				int return_date_id = rs.getInt("return_date_id");
				int borrowed_date_id = rs.getInt("borrowed_date_id");
				Book book = BookController.GetBookById(connection, book_id); 
				Subscriber subscriber = SubscriberController.getSubscriberById(connection, subscriber_id);
				LocalDate borrowed_date = DateController.GetDateById(connection, borrowed_date_id);
				LocalDate return_date = DateController.GetDateById(connection, return_date_id);
				borrowedBooks.add(new BorrowedBook(book, subscriber, borrowed_date, return_date));
			}
			return borrowedBooks;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not get all borrowed books");
			return null;
		}
	}

	public static List<Integer> GetAllNonBorrowedBookIdsBySerial(Connection connection, int bookSerialId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT b.book_id FROM book b WHERE b.book_serial_id = ? AND b.book_id NOT IN (SELECT bb.book_id FROM borrowed_book bb)");
			pstmt.setInt(1, bookSerialId);
			ResultSet rs = pstmt.executeQuery();
			List<Integer> bookIds = new ArrayList<Integer>();
			while(rs.next()) {
				int book_id = rs.getInt("book_id");
				bookIds.add(book_id);
			}
			return bookIds;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not get all non borrowed books with serial id " +bookSerialId);
			return null;
		}
	}
	
	/**
	 * If a book is borrowed it will return the borrowed book object
	 * @param connection
	 * @param book_id
	 * @return borrowedbook, null if fail or not borrowed
	 */
	public static BorrowedBook GetBorrowedBookByBookId(Connection connection, int book_id) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * from borrowed_book WHERE book_id = ?");
			pstmt.setInt(1, book_id);
			ResultSet rs = pstmt.executeQuery();
			BorrowedBook borrowedBook = null;
			if(rs.next()) {
				Book book = BookController.GetBookById(connection, rs.getInt("book_id"));
				Subscriber subscriber = SubscriberController.getSubscriberById(connection, rs.getInt("subscriber_id"));
				LocalDate from = DateController.GetDateById(connection, rs.getInt("borrowed_date_id"));
				LocalDate to = DateController.GetDateById(connection, rs.getInt("return_date_id"));
				borrowedBook = new BorrowedBook(book, subscriber, from, to);
			}
			return borrowedBook;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get a borrowed book by id");
			return null;
		}
	}
	
	/**
	 * If a bookSerialId is borrowed by a subscriberId it will return the borrowed book object
	 * @param connection
	 * @param book_serial_id
	 * @param subscriber_id
	 * @return borrowedbook, null if fail or not borrowed
	 */
	public static BorrowedBook GetBorrowedBookByBookSerialIdAndSubscriberId(Connection connection, int book_serial_id, int subscriber_id) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * from borrowed_book WHERE subscriber_id = ? AND book_id IN (SELECT book_id FROM book WHERE book_serial_id = ?)");
			pstmt.setInt(1, subscriber_id);
			pstmt.setInt(2, book_serial_id);
			ResultSet rs = pstmt.executeQuery();
			BorrowedBook borrowedBook = null;
			if(rs.next()) {
				Book book = BookController.GetBookById(connection, rs.getInt("book_id"));
				Subscriber subscriber = SubscriberController.getSubscriberById(connection, rs.getInt("subscriber_id"));
				LocalDate from = DateController.GetDateById(connection, rs.getInt("borrowed_date_id"));
				LocalDate to = DateController.GetDateById(connection, rs.getInt("return_date_id"));
				borrowedBook = new BorrowedBook(book, subscriber, from, to);
			}
			return borrowedBook;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get a borrowed book by serialid and subscriber");
			return null;
		}
	}
	
	/**
	 * Returns a list of borrowed books by a subscriber
	 * @param connection
	 * @param subscriberId
	 * @return List<BorrowedBook> borrowedBookList
	 */
	public static List<BorrowedBook> GetBorrowedBooksBySubscriberId(Connection connection, int subscriberId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT book_id FROM borrowed_book WHERE subscriber_id = ?");
			pstmt.setInt(1, subscriberId);
			ResultSet rs = pstmt.executeQuery();
			List<BorrowedBook> borrowedBookList = new ArrayList<BorrowedBook>();
			while(rs.next()) {
				int book_id = rs.getInt("book_id");
				BorrowedBook bb = LendController.GetBorrowedBookByBookId(connection, book_id);
				borrowedBookList.add(bb);
			}
			return borrowedBookList;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get a borrowed book list by subscriber");
			return null;
		}
	}
	
	/**
	 * Returns Book's Serial the closest return date
	 * @param connection
	 * @param bookSerialId
	 * @return LocalDate closestReturnDate
	 */
	public static LocalDate GetClosestReturnDateOfBookSerialId(Connection connection, int bookSerialId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
				
		try {
			List<BorrowedBook> borrowedBookList = GetAllBorrowedBooks(connection);
			LocalDate minReturnDate = null;
			for(BorrowedBook bb : borrowedBookList) {
				if(bb.getBorrowedBook().getSerial_id() == bookSerialId) { //look at only relevant borrowed books
					if(minReturnDate == null || bb.getReturnDate().isBefore(minReturnDate)) {
						minReturnDate = bb.getReturnDate();
					}
				}
			}
			return minReturnDate;
		}
		catch(Exception e) {
			System.err.println("Could not check Book's closest Return Date.");
			return null;
		}
	}
	
	/**
	 * Returns whether a certain book serial is already lent by this subscriber
	 * @param connection
	 * @param subscriberId
	 * @param bookSerialId
	 * @return -1=error, 0=false, 1=true
	 */
	public static int IsBookLentBySubscriber(Connection connection, int subscriberId, int bookSerialId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
				
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT book_id FROM book b WHERE b.book_serial_id = ? AND b.book_id IN (SELECT bb.book_id FROM borrowed_book bb WHERE bb.subscriber_id = ?)");
			pstmt.setInt(1, bookSerialId);
			pstmt.setInt(2, subscriberId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return 1;
			}
			return 0;
		}
		catch(Exception e) {
			System.err.println("Could not check if Book Lent by Subscriber.");
			return -1;
		}
	}
	
	/**
	 * Lend book to subscriber using bookSerialId from Date to Date
	 * Lends the first book received from Search Query
	 * @param connection
	 * @param subscriberId
	 * @param Date from when
	 * @param Date to when
	 * @param book serial id
	 * @return boolean -1=error, 0=fail, 1=success
	 */
	public static int LendBookSerialId(Connection connection, int subscriberId, LocalDate from, LocalDate to, int book_serial_id) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}	
		
		//Check if book is already lent
		int lent = IsBookLentBySubscriber(connection, subscriberId, book_serial_id);
		if(lent>0) {
			return 0;
		}
		
		try {
			int available = BookController.CheckBookSerialAvailability(connection, book_serial_id);
			if(available == 0) {
				return 0;
			}
			int dateFromId = DateController.GetOrCreateDateIdByDate(connection, from);
			int dateToId = DateController.GetOrCreateDateIdByDate(connection, to);
			List<Integer> booksNotBorrowed = GetAllNonBorrowedBookIdsBySerial(connection, book_serial_id);
			int firstBookId = booksNotBorrowed.get(0);
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO borrowed_book(book_id, subscriber_id, borrowed_date_id, return_date_id) VALUES (?,?,?,?)");
			pstmt.setInt(1, firstBookId);
			pstmt.setInt(2, subscriberId);
			pstmt.setInt(3, dateFromId);
			pstmt.setInt(4, dateToId);
			int success = pstmt.executeUpdate();
			if(success<=0) {
				return 0;
			}
			//RecordAction for user
			User user = UserController.getUserById(connection, subscriberId);
			DetailedHistory dh = new DetailedHistory(user, ActionType.BORROW, LocalDate.now(), "Subscriber " + subscriberId + " is borrowing bookId: " + firstBookId + " on " + DateUtil.DateToString(LocalDate.now()));
			DetailedHistoryController.RecordHistory(connection, dh);
			return 1;
		}
		catch(Exception e) {
			System.err.println("Could not LendBook using bookId");
			return -1;
		}
	}

	/**
	 * Lend book to subscriber using bookId from Date to Date
	 * @param connection
	 * @param subscriberId
	 * @param Date from when
	 * @param book id
	 * @return boolean -1=error, 0=fail, 1=success
	 */
	public static int LendBookId(Connection connection, int subscriberId, LocalDate from, LocalDate to, int book_id) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		//Check if book is already lent
		Book book = BookController.GetBookById(connection, book_id);
		if(book != null) {
			int lent = IsBookLentBySubscriber(connection, subscriberId, book.getSerial_id());
			if(lent>0) {
				return 0;
			}
		}
		
		try {
			int available = BookController.CheckBookAvailability(connection, book_id);
			if(available == 0) {
				return 0;
			}
			int dateFromId = DateController.GetOrCreateDateIdByDate(connection, from);
			int dateToId = DateController.GetOrCreateDateIdByDate(connection, to);
			if(dateFromId<=0 || dateToId <=0) {
				return -1;
			}
			PreparedStatement pstmt = connection.prepareStatement("INSERT INTO borrowed_book(book_id, subscriber_id, borrowed_date_id, return_date_id) VALUES (?,?,?,?)");
			pstmt.setInt(1, book_id);
			pstmt.setInt(2, subscriberId);
			pstmt.setInt(3, dateFromId);
			pstmt.setInt(4, dateToId);
			int success = pstmt.executeUpdate();
			if(success<=0) {
				return 0;
			}
			//RecordAction for user
			User user = UserController.getUserById(connection, subscriberId);
			DetailedHistory dh = new DetailedHistory(user, ActionType.BORROW, LocalDate.now(), "Subscriber " + subscriberId + " is borrowing bookId: " + book_id + " on " + DateUtil.DateToString(LocalDate.now()));
			DetailedHistoryController.RecordHistory(connection, dh);
			return 1;
		}
		catch(Exception e) {
			System.err.println("Could not LendBook using bookId");
			return -1;
		}
	}
	
	/**
	 * Return a book using bookId at returnDate
	 * @param connection
	 * @param bookId
	 * @param returnDate
	 * @return -1=error, 0=fail, 1=success
	 */
	public static int ReturnBook(Connection connection, Book book, LocalDate returnDate) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		try {
			BorrowedBook borrowedbook = GetBorrowedBookByBookId(connection, book.getId());
			PreparedStatement pstmt = connection.prepareStatement("DELETE FROM borrowed_book WHERE subscriber_id = ? AND book_id IN (SELECT book_id FROM book WHERE book_serial_id = ?)");
			pstmt.setInt(1, borrowedbook.getBorrowingSubscriber().getSubscriberId());
			pstmt.setInt(2, borrowedbook.getBorrowedBook().getSerial_id());
			int success = pstmt.executeUpdate();
			if(success<=0) {
				System.out.println("Could not Return book, could not delete relevant row");
				return 0;
			}
			//Record action
			User user = UserController.getUserById(connection, borrowedbook.getBorrowingSubscriber().getSubscriberId());
			DetailedHistory dh = new DetailedHistory(user,ActionType.RETURN,returnDate,"Returned bookId "+book.getId()+"");
			int result = DetailedHistoryController.RecordHistory(connection, dh);
			
			//Freeze subscriber if returned late by a week
			if(borrowedbook.getReturnDate().isAfter(returnDate.plusDays(6)) && result>0) {
				SubscriberController.SetFreezeSubscriber(connection, borrowedbook.getBorrowingSubscriber().getSubscriberId(), true);
				dh = new DetailedHistory(user, ActionType.FREEZE,returnDate,"Subscriber frozen for returning book "+book.getId()+" after return time "+DateUtil.DateToString(borrowedbook.getBorrowedDate()));
				DetailedHistoryController.RecordHistory(connection, dh);
				result = 2;
			}
			
			return result;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not return book");
			return -1;
		}
	}
	
	//DEBUG main
	public static void main(String args[]) {
		DatabaseConnection dbc;
		try {
			dbc = DatabaseConnection.getInstance();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		int subscriber_id = 1;
		LocalDate from = LocalDate.of(2025,1,24);
		LocalDate to = LocalDate.of(2025,1,30);
		
		int book_serial_id = 101;
		int success = LendBookSerialId(dbc.getConnection(), subscriber_id, from, to, book_serial_id);
		System.out.println(success);
	}
}
