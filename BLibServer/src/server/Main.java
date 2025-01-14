package server;

import java.sql.SQLException;

import common.Client;

public class Main {
    public static void main(String[] args) {
        String DBIp = "127.0.0.1"; // Replace with your DB IP
        String DBScheme = "world"; // Replace with your database schema
        String DBUser = "root"; // Replace with your DB username
        String DBPass = "HasAbd2*"; // Replace with your DB password

        try {
            // Initialize the database connection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance(DBIp, DBScheme, DBUser, DBPass);

            // Test HandleClientLogin method
            String testUsername = "bobbyj"; // Replace with a valid username from your DB
            String testPassword = "hashedpassword2"; // Replace with the corresponding password

            Client loggedInClient = dbConnection.HandleClientLogin(testUsername, testPassword);
            if (loggedInClient != null) {
            	 System.out.println(loggedInClient.toString());
            } else {
                System.out.println("Login failed. Invalid username or password.");
            }

            

            // Close the database connection
            
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
        }
    }
}