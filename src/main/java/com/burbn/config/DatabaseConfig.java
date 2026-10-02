package com.burbn.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConfig manages JDBC connection lifecycle to MySQL database.
 */
public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/socialmedia?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";
    
    public static Connection connection;

    /**
     * Establishes connection to MySQL database.
     */
    public static synchronized Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("[DB] Connected successfully to database: socialmedia");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB Error] MySQL JDBC Driver not found in classpath.");
        } catch (SQLException e) {
            System.err.println("[DB Connection Failed] " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the active database connection.
     */
    public static synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB Error] Exception while closing connection: " + e.getMessage());
        }
    }
}
