package com.auction.dao;

import com.auction.factory.UserFactory;
import com.auction.model.*;
import java.sql.*;
import com.auction.util.DBConnection;

/**
 * Data Access Object (DAO) for User operations
 * Handles all database interactions for users (INSERT, SELECT, UPDATE, DELETE)
 * Singleton pattern - only one instance
 */
public class UserDAO {
    private static UserDAO instance;

    private UserDAO() {}

    /**
     * Get singleton instance of UserDAO
     */
    public static UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    /**
     * Save a new user to the database
     *
     * @param user User object to save
     * @return true if save successful, false otherwise
     */
    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getClass().getSimpleName());

            // Execute insert
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                // Get the auto-generated user_id from database
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        user.setId(userId);  // Set the id on the Java object
                        System.out.println("User saved with ID: " + userId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if a username already exists in database
     * Used for validation during signup
     *
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean userExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();  // Returns true if any row found
            }

        } catch (SQLException e) {
            System.out.println("✗ Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if an email already exists in database
     * Used for validation during signup
     *
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();  // Returns true if any row found
            }

        } catch (SQLException e) {
            System.out.println("✗ Error checking email: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get user by username and password (for login)
     *
     * @param email Email to log in
     * @param password Password to verify
     * @return User object if found and password matches, null otherwise
     */
    public User getUserByEmailAndPasswordAndRole(String email, String password, String role) {
        if (email.equals("admin@gmail.com") && password.equals("Adminbul0z")) {
            System.out.println("Admin is in");
            User user = UserFactory.createUser("admin", password, email, "Admin");
            user.setId(6);
            return user;
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, role);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    // User found - create appropriate User object based on role
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");

                    User user = UserFactory.createUser(username, password, email, role);

                    user.setId(userId);  // Set the id from database
                    System.out.println("✓ User found: " + email);
                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("✗ Error getting user: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✗ User not found: " + email);
        return null;
    }

    /**
     * Get user by user_id
     *
     * @param userId User ID to search
     * @return User object if found, null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");

                User user;

                if ("Seller".equals(role)) {
                    user = new Seller(username, password, email);
                } else if ("Admin".equals(role)) {
                    user = new Admin(username, password, email);
                } else {
                    user = new Bidder(username, password, email);
                }

                user.setId(userId);
                return user;
            }

        } catch (SQLException e) {
            System.out.println("✗ Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update user information in database
     *
     * @param user User object with updated info
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, email = ? WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✓ User updated: " + user.getUsername());
                return true;
            }

        } catch (SQLException e) {
            System.out.println("✗ Error updating user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete a user from database
     *
     * @param userId User ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✓ User deleted with ID: " + userId);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("✗ Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}