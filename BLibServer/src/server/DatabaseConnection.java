package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import common.Subscriber;
import common.Client;

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
    public boolean addClientToDatabase(Client client) {
        Connection connection = getInstanceConnection();

        if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return false;
        }

        // Map userType string to integer (1 for librarian, 2 for client)
        int userTypeId = "librarian".equalsIgnoreCase(client.getUserType()) ? 1 : 2;

        String query = "INSERT INTO users (name, lastName, membershipId, userName, password, email, phoneNumber, userTypeId) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, client.getName());
            pstmt.setString(2, client.getLastName());
            pstmt.setString(3, client.getMembershipId());
            pstmt.setString(4, client.getUserName());
            pstmt.setString(5, client.getPassword()); // Assuming the password is already hashed
            pstmt.setString(6, client.getEmail());
            pstmt.setString(7, client.getPhoneNumber());
            pstmt.setInt(8, userTypeId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if insertion succeeded
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }

    public Client HandleClientLogin( String userName, String password) {
    	Connection connection = getInstanceConnection();

        try {
            String query = "SELECT u.*, ut.type AS userType FROM users u " +
                           "JOIN usertypes ut ON u.userTypeId = ut.UserTypeId " +
                           "WHERE u.userName = ? AND u.password = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("userId");
                String name = rs.getString("name");
                String lastName = rs.getString("lastName");
                String membershipId = rs.getString("membershipId");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phoneNumber");
                String userType = rs.getString("userType"); // Retrieve the user type as a string

                // Create and return the Client object
                return new Client(userId, name, lastName, membershipId, userName, password, email, phoneNumber, userType);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return null; // Return null if no match is found
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

