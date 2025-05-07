package com.furnituredesigner.server.service;

import com.furnituredesigner.server.db.DatabaseManager;
import com.furnituredesigner.common.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserService {
    
    public boolean isSuperAdminExists() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'superadmin'";
        boolean exists = false;
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
                System.out.println("SuperAdmin check: " + (exists ? "exists" : "does not exist"));
                
                // If exists, print all users for debugging
                if (exists) {
                    printAllUsers(conn);
                }
            }
        }
        
        return exists;
    }
    
    private void printAllUsers(Connection conn) throws SQLException {
        System.out.println("--- All Users in Database ---");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, full_name, email, role FROM users")) {
            
            while (rs.next()) {
                System.out.println(String.format("ID: %d, Username: %s, Name: %s, Email: %s, Role: %s",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role")
                ));
            }
        }
        System.out.println("----------------------------");
    }
    
    public User createSuperAdmin(String username, String password, String fullName, String email) throws SQLException {
        // If super admin exists, return null
        if (isSuperAdminExists()) {
            System.out.println("Cannot create SuperAdmin: One already exists");
            return null;
        }
        
        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, 'superadmin')";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Hash the password with BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, fullName);
            stmt.setString(4, email);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("SuperAdmin created successfully: " + username);
                // Fetch the newly created user
                return getUserByUsername(username);
            }
        }
        
        System.out.println("Failed to create SuperAdmin account");
        return null;
    }
    
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, full_name, email, role FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
            }
        }
        
        return null;
    }
}