package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import common.Subscriber;

public class DatabaseConnection {
    private static DatabaseConnection instance; // Singleton instance
    private Connection connection; // Single database connection

    private DatabaseConnection(String DBIp, String DBScheme, String DBUser, String DBPass) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeeded.");
        } catch (Exception ex) {
            System.out.println("Driver definition failed: " + ex.getMessage());
            throw new SQLException("Failed to load driver.");
        }

        connection = DriverManager.getConnection(
            "jdbc:mysql://"+DBIp+"/"+DBScheme+"?serverTimezone=IST", DBUser, DBPass);
        System.out.println("SQL connection succeeded.");
    }

    public static DatabaseConnection getInstance(String DBIp, String DBScheme, String DBUser, String DBPass) throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection(DBIp, DBScheme, DBUser, DBPass);
                }
            }
        }
        return instance;
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                instance = null;
                System.out.println("Database connection closed successfully.");
            } catch (SQLException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
    public Connection getConnection() {
        return connection;
    }

    public  ArrayList<Subscriber> getAllSubscribers() {
        ArrayList<Subscriber> subscribers = new ArrayList<>();
        Connection connection = getInstanceConnection();

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

    public  boolean updateSubscriber(int subscriberId, String newEmail, String newPhoneNumber) {
        Connection connection = getInstanceConnection();
        
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
    public Subscriber getSubscriberById(String subscriberId) {
        Connection connection = getInstanceConnection();
        
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


    private static Connection getInstanceConnection() {
        try {
            return (instance != null) ? instance.getConnection() : null;
        } catch (Exception ex) {
            System.out.println("Error getting connection: " + ex.getMessage());
            return null;
        }
    }
}
