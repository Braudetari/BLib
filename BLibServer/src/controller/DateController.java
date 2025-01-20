package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import javax.print.attribute.standard.DateTimeAtCompleted;

import common.Book;
import common.Subscriber;

public class DateController {
	/**
	 * Get Date from Database using ID
	 * @param connection
	 * @param dateId
	 * @return Book
	 */
	@SuppressWarnings("deprecation")
	public static Date GetDateById(Connection connection, int dateId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM date WHERE date_id = ?");
			pstmt.setInt(1, dateId);
			ResultSet rs = pstmt.executeQuery();
			Date date = null;
			if(rs.next()) {
				int day = rs.getInt("day");
				int month = rs.getInt("month");
				int year = rs.getInt("year");
				date = new Date(year, month, day);
			}
			return date;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Get Book from Database (ID)");
			return null;
		}
	}
	
	/**
	 * Gets DateId from given Date in database
	 * @param connection
	 * @param date
	 * @return -1=error, 0=fail, >0=date_id
	 */
	@SuppressWarnings("deprecation")
	public static int GetDateIdByDate(Connection connection, Date givenDate) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		try {
			//Get Date
			String query = "SELECT date_id ROM date WHERE year=? AND month=? and day=?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, givenDate.getYear());
			pstmt.setInt(2, givenDate.getMonth());
			pstmt.setInt(3, givenDate.getDay());
			ResultSet rs = pstmt.executeQuery();
			int dateId = 0;
			if(rs.next()) {
				dateId = rs.getInt("date_id");
			}
			return dateId;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Get Date from Database");
			return -1;
		}
	}
	
	/**
	 * Creates DateId from given Date in database and returns it
	 * @param connection
	 * @param date
	 * @return -1=error, 0=fail, 1=success
	 */
	@SuppressWarnings("deprecation")
	public static int CreateDateIdByDate(Connection connection, Date givenDate) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		
		try {
			//Get Date
			String query = "INSERT INTO date(year,month,day) VALUES(?,?,?)";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, givenDate.getYear());
			pstmt.setInt(2, givenDate.getMonth());
			pstmt.setInt(3, givenDate.getDay());
			int success = pstmt.executeUpdate();
			return (success>0) ? 1 : 0;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
			System.err.println("Could not Create Date from Database");
			return -1;
		}
	}
}
