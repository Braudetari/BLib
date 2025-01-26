package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import common.BorrowedBook;

public class ExtendController {
	
	/**
	 * Checks if a Book is Extendable
	 * @param connection
	 * @param bookId
	 * @return -1=fail, 0=nope, 1=yep
	 */
	public static int IsBookExtendable(Connection connection, int bookId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		try {
			BorrowedBook bb = LendController.GetBorrowedBookByBookId(connection, bookId);
			//Is the book returnDate within a week
			if(LocalDate.now().isBefore(bb.getReturnDate().minusDays(7)) ||
					LocalDate.now().isEqual(bb.getReturnDate().minusDays(7))){
				return 0;
			}
			PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM `order` WHERE book_id = ?");
			pstmt.setInt(1, bookId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){ //book already ordered
				return 0;
			}
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not check if book is extendable");
			return -1;
		}
	}
	
	/**
	 * Extend a book's Return Date by up to 2 weeks
	 * @param connection
	 * @param bookId
	 * @return Date of new Return Date
	 */
	public static LocalDate ExtendBookReturnDate(Connection connection, int bookId, int amountOfDays) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		try {
			//Check if Book is Extendable
			if(IsBookExtendable(connection, bookId)<=0) {
				throw new Exception();
			}
			//Check that amountOfDays is <=14
			if(amountOfDays > 14) {
				return null;
			}
			BorrowedBook bb = LendController.GetBorrowedBookByBookId(connection, bookId);
			LocalDate date = bb.getReturnDate();
			int dateId = DateController.GetOrCreateDateIdByDate(connection, date.plusDays(amountOfDays));
			PreparedStatement pstmt = connection.prepareStatement("UPDATE borrowed_book SET return_date_id = ? WHERE book_id = ?");
			pstmt.setInt(1, dateId);
			pstmt.setInt(2, bookId);
			int success = pstmt.executeUpdate();
			if(success <= 0)
				return null;
			//Record Extension
			return date.plusDays(amountOfDays);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not extend book");
			return null;
		}
	}
}
