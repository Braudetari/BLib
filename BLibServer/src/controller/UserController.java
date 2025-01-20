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
                String password = rs.getString("password");
                User.UserType type = User.UserType.fromInt(rs.getInt("user_type"));
                
                // Return User object
                return new User(username, password, type);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }

        return null; // Return null if no subscriber is found
    }
}
