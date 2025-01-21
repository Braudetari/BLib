package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.*;

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
				Date borrowed_date = DateController.GetDateById(connection, borrowed_date_id);
				Date return_date = DateController.GetDateById(connection, return_date_id);
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
}
