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

 

    private static Connection getInstanceConnection() {
        try {
            return (instance != null) ? instance.getConnection() : null;
        } catch (Exception ex) {
            System.out.println("Error getting connection: " + ex.getMessage());
            return null;
        }
    }
}
