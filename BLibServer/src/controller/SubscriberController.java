package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.Subscriber;

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
	    public static boolean updateSubscriber(Connection connection, int subscriberId, String newEmail, String newPhoneNumber) {
	        
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
	    public static Subscriber getSubscriberById(Connection connection, String subscriberId) {
	        
	        if (connection == null) {
	            System.out.println("Failed to connect to the database.");
	            return null;
	        }

	        try {
	            String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
	            PreparedStatement pstmt = connection.prepareStatement(query);
	            pstmt.setString(1, subscriberId); // Use setString for a String parameter

	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                String subscriberName = rs.getString("subscriber_name");
	                int detailedSubscriptionHistory = rs.getInt("detailed_subscription_history");
	                String subscriberPhoneNumber = rs.getString("subscriber_phone_number");
	                String subscriberEmail = rs.getString("subscriber_email");

	                // Return the relevant Subscriber object
	                return new Subscriber(Integer.parseInt(subscriberId), subscriberName, detailedSubscriptionHistory, subscriberPhoneNumber, subscriberEmail);
	            }
	        } catch (SQLException ex) {
	            System.out.println("SQLException: " + ex.getMessage());
	        }

	        return null; // Return null if no subscriber is found
	    }

}
