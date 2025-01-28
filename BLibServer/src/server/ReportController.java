package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import common.*;

import controller.SubscriberController;

public class ReportController {
	
	/**
	 * Generates Subscriber Report of how many frozen 
	 * @param connection
	 * @return Object[]= {(int)ActiveCount, (int)FrozenCount}
	 */
	public static Object[] GenerateSubscriberReport(Connection connection) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return null;
		}
		try {
			List<Subscriber> subscriberList = SubscriberController.getAllSubscribers(connection);
			int frozen = 0;
			for(Subscriber subscriber : subscriberList) {
				if(subscriber.isFrozen()) {
					frozen++;
				}
			}
			int active = subscriberList.size()-frozen;
			return new Object[] {active, frozen};
		}
		catch(Exception e) {
			System.err.println("Could not generate subscriber report");
			return null;
		}
	}
	
	/**
	 * Generate Loan Time Report of Book Genre's to Average Loan Time
	 * @param connection
	 * @param year
	 * @param month
	 * @return Object[2] = {List<String>bookGenre, List<Integer>loanTime}
	 */
	@SuppressWarnings("unchecked")
	public static Object[] GenerateLoanTimeReport(Connection connection, int year, int month) {
		if(connection == null) {
			System.err.println("Could not connect to database");
			return null;
		}
		if(month < 1 || month > 12) {
			System.err.println("Invalid month and year to generate loan report");
			return null;
		}
		try {
			Object[] reportObject = GetReportBlobFromDatabase(connection, year, month);
			if(reportObject == null) {
				throw new Exception();
			}
			List<Book> bookList = (List<Book>)reportObject[0];
			List<Integer> loanList = (List<Integer>)reportObject[1];
			List<String> genreList = new ArrayList<String>();
			List<Integer> countList = new ArrayList<Integer>();
			List<Double> avgLoanList = new ArrayList<Double>();
			
			for (int i = 0; i < bookList.size(); i++) {
	            Book currentBook = bookList.get(i);
	            Double currentLoanCount = loanList.get(i).doubleValue();
	            String genre = currentBook.getGenre();

	            if (genre == null || genre.trim().isEmpty()) {
	                continue;
	            }
	            genre = genre.trim();
	            int genreIndex = genreList.indexOf(genre);

	            if (genreIndex == -1) {
	                genreList.add(genre);
	                countList.add(1);
	                avgLoanList.add(currentLoanCount);
	            } else {
	                countList.set(genreIndex, countList.get(genreIndex) + 1);
	                avgLoanList.set(genreIndex, avgLoanList.get(genreIndex) + currentLoanCount);
	            }
	        }
	        for (int i = 0; i < avgLoanList.size(); i++) {
	            Double totalLoans = avgLoanList.get(i).doubleValue();
	            int count = countList.get(i);
	            Double average = (count > 0) ? totalLoans / count : 0;

	            avgLoanList.set(i, average);
	        }
	        
	        return new Object[] {genreList, avgLoanList};
		}
		catch(Exception e) {
			System.err.println("Could not generate loan report");
			return null;
		}
	}
	
	/**
	 * Returns Report Blob from Database
	 * @param connection
	 * @param year
	 * @param month
	 * @return Object[] = {List<Book> books, List<Integer> loanTime}
	 */
	public static Object[] GetReportBlobFromDatabase(Connection connection, int year, int month) {
		if(connection == null) {
			System.err.println("Coult not connect to database");
			return null;
		}
		if(month > 12 || month < 1) {
			System.err.println("Get Report Blob: Invalid month/year");
			return null;
		}
		try {
			String query = "SELECT report_blob FROM report WHERE month = ? AND year = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, month);
			pstmt.setInt(2, year);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()) {
				System.out.println("There are reports in Database on " + month+"/"+year );
				return null;
			}
			byte[] blobBytes = rs.getBytes("report_blob");
			if(blobBytes == null) {
				return null;
			}
			try{
				ByteArrayInputStream bis = new ByteArrayInputStream(blobBytes);
				ObjectInputStream ois = new ObjectInputStream(bis);
				@SuppressWarnings("unchecked")
				Object[] reportObject = (Object[])ois.readObject();
				return reportObject;
			}
			catch(Exception e) {
				return null;
			}
		}
		catch(Exception e) {
			System.err.println("Could not get notification List from Database");
			return null;
		}
	}
	
	/**
	 * Updates Report Blob to Database
	 * @param connection
	 * @param reportObject
	 * @param year
	 * @param month
	 * @return
	 */
	public static int UpdateReportBlobInDatabase(Connection connection, Object[] reportObject, int year, int month) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(month > 12 || month < 1) {
			System.err.println("Invalid month/year for report");
			return -1;
		}
		if(reportObject == null) {
			System.err.println("No Report Object List was provided");
			return -1;
		}
		//Serialize List into Bytes (our Blob)
		byte[] blobBytes = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(reportObject);
			oos.flush();
			blobBytes = bos.toByteArray();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not serialize Report Object List.");
			return -1;
		}
		
		//Store (or Create) notification List as Blob in Database
		try {
			PreparedStatement pstmt;
			Object[] reportObjectTest = GetReportBlobFromDatabase(connection, year, month);
			String query;
			if(reportObjectTest == null) {
				query = "INSERT INTO report (report_blob, month, year) VALUES (?,?,?)";
			}
			else {
				query = "UPDATE report SET report_blob = ? WHERE month = ? AND year = ?";
			}
			pstmt = connection.prepareStatement(query);
			pstmt.setBytes(1, blobBytes);
			pstmt.setInt(2, month);
			pstmt.setInt(3, year);
			int success = pstmt.executeUpdate();
			if(success<=0) { //couldnt update
				System.err.println("Failed to Insert Report Object into Database");
				return 0;
			}
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not update Notifications List in database");
			return -1;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static int AddReportToDatabase(Connection connection, Book book, int loanTime) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		LocalDate today = LocalDate.now();
		Object[] reportObject = GetReportBlobFromDatabase(connection, today.getYear(), today.getMonthValue());
		if(reportObject == null) {
			reportObject = new Object[] {new ArrayList<Book>(), new ArrayList<Integer>()};
		}
		((List<Book>)reportObject[0]).add(book);
		((List<Integer>)reportObject[1]).add(loanTime);
		int success = UpdateReportBlobInDatabase(connection, reportObject, today.getYear(), today.getMonthValue());
		if(success <= 0) {
			return 0;
		}
		return 1;
	}
}
