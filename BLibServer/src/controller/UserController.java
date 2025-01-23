package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.Subscriber;
import common.User;
import server.DatabaseConnection;

public class UserController {
	public static User getUserByUsername(Connection connection, String username) {
        
        if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return null;
        }

        try {
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, username); // Use setString for a String parameter

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	int id = rs.getInt("user_id");
                String password = rs.getString("password");
                User.UserType type = User.UserType.fromString(rs.getString("user_type"));
                
                // Return User object
                return new User(id, username, password, type);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return null; // Return null if no subscriber is found
    }

	public static User getUserById(Connection connection, int userId) {
		if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return null;
        }

        try {
            String query = "SELECT * FROM user WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userId); // Use setString for a String parameter

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	int id = rs.getInt("user_id");
            	String username = rs.getString("username");
                String password = rs.getString("password");
                User.UserType type = User.UserType.fromString(rs.getString("user_type"));
                
                // Return User object
                return new User(id, username, password, type);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.err.println("Could not get User by ID.");
        }

        return null; // Return null if no subscriber is found
	}
	
	//INSERT given user into database
	public static boolean createUsername(Connection connection, User user) {
        
        if (connection == null) {
            System.out.println("Failed to connect to the database.");
            return false;
        }

        try {
            String query = "INSERT INTO user (username, password, user_type) VALUES(?,?,?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, user.getUsername()); // Use setString for a String parameter
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getType().toString().toLowerCase());
            int success = pstmt.executeUpdate();

            return (success>0) ? true : false;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            return false;
        }
    }
	/**
	 * Get's User's Name, goes through Librarian and Subscriber
	 * Returns "No name" if name is Null or empty
	 * @param connection
	 * @param user
	 * @return String user's name
	 */
	public static String getNameFromUser(Connection connection, User user) {
		if (connection == null) {
            System.err.println("Failed to connect to the database.");
            return null;
        }
		if(user == null) {
			System.err.println("Not a valid user");
			return null;
		}
		
		try {
			String query;
			switch(user.getType().toString()) {
			case "LIBRARIAN":
					query = "SELECT librarian_name FROM librarian WHERE librarian_id = ?";
				break;
			case "SUBSCRIBER":
					query = "SELECT subscriber_name FROM subscriber WHERE subscriber_id = ?";
				break;
			case "GUEST":
					return new String("GUEST");
			default:
				throw new Exception();
			}
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, user.getId());
			ResultSet rs = pstmt.executeQuery();
			String name = "No name";
			if(rs.next()) {
				name = rs.getString(1);
			}
			return name;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get user's name");
			return null;
		}
	}

}
