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

}
