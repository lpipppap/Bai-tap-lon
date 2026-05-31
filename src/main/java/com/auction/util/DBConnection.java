package com.auction.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class to manage MySQL database connections
 * Singleton pattern - only one connection at a time
 */
public class DBConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://baitaplophethongdaugia-iykyk.l.aivencloud.com:24578/defaultdb?useSSL=true&trustServerCertificate=true";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_Nx4hitlqN_bgaDvm8R7";

    private static Connection connection;

    /**
     * Get a connection to the database
     * Creates a new connection if one doesn't exist
     *
     * @return Connection object to MySQL database
     * @throws SQLException if connection fails
     */
    private static final ThreadLocal<Connection> threadLocalConn = new ThreadLocal<>();

    public static Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException("Driver not found"); }

        Connection conn = threadLocalConn.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            threadLocalConn.set(conn);
            System.out.println("✓ New DB connection for thread: " + Thread.currentThread().getName());
        }
        return conn;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        try {
            Connection conn = threadLocalConn.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("✓ DB connection closed for thread: " + Thread.currentThread().getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            threadLocalConn.remove();
        }
    }

    /**
     * Test if connection is alive
     * @return true if connected, false otherwise
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}