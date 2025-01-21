package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.Subscriber;
import common.User;
import common.User.UserType;

public class SubscriberController {
	   public static ArrayList<Subscriber> getAllSubscribers(Connection connection) {
	        ArrayList<Subscriber> subscribers = new ArrayList<>();

	        if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return subscribers;
	        }

	        try (Statement stmt = connection.createStatement()) {
	            String query = "SELECT * FROM subscriber";
	            ResultSet rs = stmt.executeQuery(query);

	            while (rs.next()) {
	                int subscriberId = rs.getInt("subscriber_id");
	                String subscriberName = rs.getString("subscriber_name");
	                int detailedSubscriptionHistory = rs.getInt("detailed_subscription_history");
	                String subscriberPhoneNumber = rs.getString("subscriber_phone_number");
	                String subscriberEmail = rs.getString("subscriber_email");

	                // Create Subscriber object and add to the list
	                Subscriber subscriber = new Subscriber(subscriberId, subscriberName, detailedSubscriptionHistory, subscriberPhoneNumber, subscriberEmail);
	                subscribers.add(subscriber);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }

	        return subscribers;
	    }
	    public static boolean updateSubscriberInfo(Connection connection, int subscriberId, String newEmail, String newPhoneNumber) {
	        
	        if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return false;
	        }

	        try {
	            String query = "UPDATE subscriber SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";
	            PreparedStatement pstmt = connection.prepareStatement(query);
	            pstmt.setString(1, newPhoneNumber);
	            pstmt.setString(2, newEmail);
	            pstmt.setInt(3, subscriberId);

	            int rowsAffected = pstmt.executeUpdate();
	            return rowsAffected > 0;
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	            return false;
	        }
	    }
	    public static Subscriber getSubscriberById(Connection connection, int subscriberId) {     
	        if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return null;
	        }

	        try {
	            String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
	            PreparedStatement pstmt = connection.prepareStatement(query);
	            pstmt.setInt(1, subscriberId); // Use setString for a String parameter

	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                String subscriberName = rs.getString("subscriber_name");
	                int detailedSubscriptionHistory;
	                try {
		                detailedSubscriptionHistory = rs.getInt("detailed_subscription_history");	
	                }
	                catch(Exception e) {
	                	detailedSubscriptionHistory = 0;
	                }
	                String subscriberPhoneNumber = rs.getString("subscriber_phone_number");
	                String subscriberEmail = rs.getString("subscriber_email");

	                // Return the relevant Subscriber object
	                return new Subscriber(subscriberId, subscriberName, detailedSubscriptionHistory, subscriberPhoneNumber, subscriberEmail);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }

	        return null; // Return null if no subscriber is found
	    }
	    public static Subscriber getSubscriberByName(Connection connection, String subscriberNameInput) {     
	        if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return null;
	        }

	        try {
	            String query = "SELECT * FROM subscriber WHERE subscriber_name = ?";
	            PreparedStatement pstmt = connection.prepareStatement(query);
	            pstmt.setString(1, subscriberNameInput); // Use setString for a String parameter

	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	            	int subscriberId = rs.getInt("subscriber_id");
	                String subscriberName = rs.getString("subscriber_name");
	                int detailedSubscriptionHistory = rs.getInt("detailed_subscription_history");
	                String subscriberPhoneNumber = rs.getString("subscriber_phone_number");
	                String subscriberEmail = rs.getString("subscriber_email");

	                // Return the relevant Subscriber object
	                return new Subscriber(subscriberId, subscriberName, detailedSubscriptionHistory, subscriberPhoneNumber, subscriberEmail);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }

	        return null; // Return null if no subscriber is found
	    }
	    
	    /**
	     * Register new subscriber,
	     * Creates new user with username and password
	     * Creates new subscriber based on the new user id
	     * @param connection
	     * @param username
	     * @param password
	     * @param name
	     * @param email
	     * @param phone
	     * @return boolean success
	     */
	    public static boolean RegisterSubscriber(Connection connection, String username, String password, String name, String email, String phone) {
	    	if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return false;
	        }
	    	User newUser = new User(0, username, password, UserType.SUBSCRIBER);
	    	UserController.createUsername(connection, newUser);
	    	User user = UserController.getUserByUsername(connection, username);
	    	
	    	//Add subscriber to database
	        try {
	            String query = "INSERT INTO subscriber (subscriber_id, subscriber_name, detailed_subscription_history, subscriber_phone_number, subscriber_email , subscriber_frozen) VALUES (?,?,?,?,?,?)";
	            PreparedStatement pstmt = connection.prepareStatement(query);
	            pstmt.setInt(1, user.getId()); 
	            pstmt.setString(2, name);
	            pstmt.setInt(3, 0);
	            pstmt.setString(4, phone);
	            pstmt.setString(5, email);
	            pstmt.setInt(6, 0);

	            int success = pstmt.executeUpdate();

	            return (success>0) ? true : false;
	        }
	        catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	            return false;
	        }

	    }

	    /**
	     * Checks in database and returns whether subscriber is frozen or not
	     * @param connection
	     * @param subscriberId
	     * @return -1=error, 0=false, 1=true
	     */
	    public static int CheckSubscriberStatus(Connection connection, int subscriberId){
	    	 if (connection == null) {
		            System.out.println("Failed to connect to the database.");
		            return -1;
		        }

		        try {
		        	PreparedStatement pstmt = connection.prepareStatement("SELECT subscriber_frozen FROM subscriber WHERE subscriber_id = ?");
		        	pstmt.setInt(1, subscriberId);
		        	ResultSet rs = pstmt.executeQuery();
		        	int frozenStatus = -1;
		        	if(rs.next()) {
		        		frozenStatus = rs.getInt(0);
		        	}
		        	return frozenStatus;
		        }
		        catch (SQLException ex) {
		        	ex.printStackTrace();
		        	System.err.println("Could not check Subscriber Status");
		        	return -1;
		        }
	    }

	    /**
	     * Set Subscriber as freeze boolean in Database
	     * if 1 freezes if 0 unfreezes
	     * @param connection
	     * @param subscriberId
	     * @param freeze
	     * @return boolean success
	     */
	    public static boolean SetFreezeSubscriber(Connection connection, int subscriberId, boolean freeze) {
	    	if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return false;
	        }

	        try {
	        	PreparedStatement pstmt = connection.prepareStatement("UPDATE subscriber SET subscriber_frozen = ? WHERE subscriber_id = ?");
	        	pstmt.setInt((freeze) ? 1 : 0, subscriberId);
	        	int success = pstmt.executeUpdate();
	        	return (success>0) ? true : false;
	        }
	        catch (SQLException ex) {
	        	ex.printStackTrace();
	        	System.err.println("Could not check Subscriber Status");
	        	return false;
	        }
	    }

	    
}
