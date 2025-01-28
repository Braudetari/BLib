package controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import common.DetailedHistory;
import common.DetailedHistory.ActionType;
import common.Subscriber;
import common.User;
import server.DatabaseConnection;

public class DetailedHistoryController {
	
	/**
	 * Update History Blob in Database, if history_id = 0 then create one and return it
	 * @param connection
	 * @param dhList  List<detailedHistory>
	 * @param historyType UserType of history
	 * @param historyId
	 * @return int -1=error, 0=fail, >0=history_id (success)
	 */
	public static int UpdateHistoryListInDatabase(Connection connection, List<DetailedHistory> dhList, User.UserType historyType, int historyId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(dhList == null) {
			System.err.println("No History List was provided");
			return -1;
		}
		//Serialize List into Bytes (our Blob)
		byte[] blobBytes = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(dhList);
			oos.flush();
			blobBytes = bos.toByteArray();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not serialize History List.");
			return -1;
		}
		
		//Store (or Create) History List as Blob in Database
		try {
			PreparedStatement pstmt;
			String query;
			if(historyId == 0) {
				query = "INSERT INTO history(history_blob, history_type) VALUES (?,?)";
				pstmt = connection.prepareStatement(query);
			}
			else {
				query = "UPDATE history SET history_blob = ? , history_type = ?  WHERE history_id = ?";
				pstmt = connection.prepareStatement(query);
				pstmt.setInt(3, historyId);
			}
			pstmt.setBytes(1, blobBytes);
			pstmt.setString(2, historyType.toString().toLowerCase());
			int success = pstmt.executeUpdate();
			if(success==0) { //couldnt update
				System.err.println("Failed to Insert History into Database");
				return 0;
			}
			//get history_id and return it
			if(historyId==0) {
				ResultSet st = pstmt.getGeneratedKeys();
				return st.getInt(1);
			}
			return historyId;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not update History List in database");
			return -1;
		}
	}
	
	/**
	 * Create History Blob in Database, simpler reuse for Update
	 * @param connection
	 * @param dhList	List<DetailedHistory>
	 * @param historyType UserType
	 * @return int same as UpdateHistory
	 */
	public static int CreateHistoryListInDatabase(Connection connection, List<DetailedHistory> dhList, User.UserType historyType) {
		int result = UpdateHistoryListInDatabase(connection, dhList, historyType, 0);
		return result;
	}
	
	/**
	 * Gets History Id for user, whether its librarian or subscriber
	 * @param connection
	 * @param user
	 * @return
	 */
	public static int GetHistoryIdForUser(Connection connection, User user) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(user == null || user.getId()<=0) {
			System.out.println("No valid userId was provided for historyId retrieval");
			return 0;
		}
		try {
			//GET history_id from USER (either subscriber or librarian)
			PreparedStatement pstmt;
			if(user.getType().equals(User.UserType.LIBRARIAN)) { //is librarian
				pstmt = connection.prepareStatement("SELECT detailed_librarian_history FROM librarian");
			}
			else {
				pstmt = connection.prepareStatement("SELECT detailed_subscription_history FROM subscriber WHERE subscriber_id = ?");
				pstmt.setInt(1, user.getId());
			}
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()){
				System.out.println("User history id retrieval failed for user");
				return 0;
			}
			return rs.getInt(1);
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not update history_id for User");
			return -1;
		}
	}
	
	
	
	/**
	 * Get History List from Database using historyId
	 * @param connection
	 * @param historyId
	 * @return List<DetailedHistory>
	 */
	public static List<DetailedHistory> GetHistoryListFromDatabase(Connection connection, int historyId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		if(historyId == 0) {
			System.err.println("Valid historyId not provided");
			return null;
		}
		
		try {
			String query = "SELECT history_blob FROM history WHERE history_id = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, historyId);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()) {
				System.out.println("There is no such history_id in Database history: " + historyId);
				return null;
			}
			byte[] blobBytes = rs.getBytes("history_blob");
			if(blobBytes == null) {
				System.err.println("Could not get history_blob from database");
				return null;
			}
			try{
				ByteArrayInputStream bis = new ByteArrayInputStream(blobBytes);
				ObjectInputStream ois = new ObjectInputStream(bis);
				@SuppressWarnings("unchecked")
				List<DetailedHistory> historyList = (List<DetailedHistory>)ois.readObject();
				return historyList;
			}
			catch(Exception e) {
				e.printStackTrace();
				System.err.println("Could not Serialize Bytes back into List<DetailedHistory>");
				return null;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get history List from Database");
			return null;
		}
	}

	/**
	 * Get History List of Librarians from Database
	 * @param connection
	 * @return List<DetailedHistory>
	 */
	public static List<DetailedHistory> GetLibrarianHistoryListFromDatabase(Connection connection){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		try {
			PreparedStatement pstmt = connection.prepareStatement("SELECT history_id FROM history WHERE history_type LIKE '%librarian%'");
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()) {
				System.out.println("Librarian History List doesnt exist in Database.");
				return null;
			}
			int librarianHistoryId = rs.getInt("history_id");
			List<DetailedHistory> historyList = GetHistoryListFromDatabase(connection, librarianHistoryId);
			return historyList;
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not get librarian history list");
			return null;
		}
	}
	
	/**
	 * Updates User's history id (whether librarian or subscriber)
	 * @param connection
	 * @param user
	 * @param historyId
	 * @return int -1=error, 0=fail, 1=success
	 */
	public static int UpdateHistoryIdForUser(Connection connection, User user, int historyId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(user == null || user.getId()<=0) {
			System.out.println("No valid userId was provided for historyId update");
			return 0;
		}
		try {
			//GET history_id from USER (either subscriber or librarian)
			PreparedStatement pstmt;
			if(user.getType().equals(User.UserType.LIBRARIAN)) { //is librarian
				pstmt = connection.prepareStatement("UPDATE librarian SET detailed_librarian_history = ? WHERE librarian_id = ?");
			}
			else {
				pstmt = connection.prepareStatement("UPDATE subscriber SET detailed_subscription_history = ? WHERE subscriber_id = ?");
			}
			pstmt.setInt(1, historyId);
			pstmt.setInt(2, user.getId());
			int result = pstmt.executeUpdate();
			if(result<=0){
				System.out.println("User history id update failed for userId" + user.getId());
				return 0;
			}
			return 1;
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not update history_id for User " + user.getId());
			return -1;
		}
	}
	
	/**
	 * Records Given DetailedHistory into Database
	 * @param connection
	 * @param dh DetailedHistory
	 * @return int -1=error, 0=fail, 1=success
	 */
	public static int RecordHistory(Connection connection, DetailedHistory dh) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(dh == null) {
			System.err.println("No DetailedHistory was provided");
			return -1;
		}
		
		try {
			//add History to Librarian, add history to all librarians if doesnt exist
			List<DetailedHistory> librarianHistory;
			
			librarianHistory = GetLibrarianHistoryListFromDatabase(connection);
			if(librarianHistory == null) {//no notification DetailedHistoryId available for Librarians, add one
				librarianHistory = new ArrayList<DetailedHistory>();
				int resultId = CreateHistoryListInDatabase(connection, librarianHistory, User.UserType.LIBRARIAN);
				if(resultId <=0) {
					return resultId;
				}
				//Update for each librarian the new historyId
				PreparedStatement pstmt = connection.prepareStatement("SELECT librarian_id FROM librarian");
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					int librarianId = rs.getInt("librarian_id");
					User user = UserController.getUserById(connection, librarianId);
					int result = UpdateHistoryIdForUser(connection, user, resultId);
					if(result<=0) {
						System.out.println("Could not update historyId for librarian "+librarianId);
					}
				}
			}
			//DetailedHistory for Librarian now exists.
			//Get History List for relevant DetailedHistory
			int userId = dh.getUser().getId();
			Subscriber subscriber = SubscriberController.getSubscriberById(connection, userId);
			int result;
			List<DetailedHistory> subscriberHistory = GetHistoryListFromDatabase(connection, subscriber.getDetailedSubscriptionHistory());
			if(subscriberHistory == null) { //doesnt exist, create one
				subscriberHistory = new ArrayList<DetailedHistory>();
				int resultId = CreateHistoryListInDatabase(connection, subscriberHistory, User.UserType.SUBSCRIBER);
				if(resultId <=0) {
					return resultId;
				}
				//update new historyId for subscriber
				result = UpdateHistoryIdForUser(connection, dh.getUser(), resultId);
			}
			//Update subscriber history
			subscriberHistory.add(dh);
			int historyId = GetHistoryIdForUser(connection, dh.getUser());
			int resultSubscriber = UpdateHistoryListInDatabase(connection, subscriberHistory, dh.getUser().getType(), historyId);
			if(resultSubscriber<=0) {
				System.out.println("Could not update history for subscriber");
			}
			//Update librarian's history
			librarianHistory = GetLibrarianHistoryListFromDatabase(connection);
			librarianHistory.add(dh);
			//generic librarian user will update for all
			historyId = GetHistoryIdForUser(connection, new User(420,"LIBRARIAN","LIBRARIAN",User.UserType.LIBRARIAN));
			int resultLib = UpdateHistoryListInDatabase(connection, librarianHistory, User.UserType.LIBRARIAN, historyId);
			if(resultLib <= 0) {
				System.out.println("Could not update history for librarian");
			}
			if(resultSubscriber <= 0 && resultLib <= 0)
				return 0;
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not record history.");
			return -1;
		}
	}
}
