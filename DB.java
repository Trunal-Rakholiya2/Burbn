package db;

import java.sql.*;

public class DB {
    static final String URL = "jdbc:mysql://127.0.0.1:3306/socialmedia";
    static final String USER = "root";
    static final String PASS = "";
    public static Connection con;

    public static void connect() {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to DB");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
    }
    public static void close() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error closing DB: " + e.getMessage());
        }
    }
}
