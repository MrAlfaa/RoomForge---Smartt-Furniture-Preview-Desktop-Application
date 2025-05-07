package com.furnituredesigner.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseManager {
    private static final String DB_DIRECTORY = "database";
    private static final String DB_FILE = DB_DIRECTORY + File.separator + "furniture_designer.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_FILE;
    
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        // Create database directory if it doesn't exist
        File dbDir = new File(DB_DIRECTORY);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        
        // Initialize database if it doesn't exist
        File dbFile = new File(DB_FILE);
        if (!dbFile.exists()) {
            initializeDatabase();
        } else {
            // Check and add role column if it doesn't exist
            updateDatabaseSchema();
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_STRING);
    }
    
    private void updateDatabaseSchema() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check if role column exists, if not add it
            try {
                stmt.execute("SELECT role FROM users LIMIT 1");
            } catch (SQLException e) {
                // Role column doesn't exist, add it
                stmt.execute("ALTER TABLE users ADD COLUMN role TEXT DEFAULT 'designer'");
                // Set admin role for admin user
                stmt.execute("UPDATE users SET role = 'admin' WHERE username = 'admin'");
                System.out.println("Database schema updated: added role column");
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table with role column
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "full_name TEXT," +
                    "email TEXT," +
                    "role TEXT DEFAULT 'designer'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            // Create rooms table
            stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "width REAL NOT NULL," +
                    "length REAL NOT NULL," +
                    "height REAL NOT NULL," +
                    "color TEXT," +
                    "user_id INTEGER," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");
            
            // Create furniture table
            stmt.execute("CREATE TABLE IF NOT EXISTS furniture (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "room_id INTEGER," +
                    "type TEXT NOT NULL," +
                    "x_pos REAL," +
                    "y_pos REAL," +
                    "z_pos REAL," +
                    "width REAL," +
                    "length REAL," +
                    "height REAL," +
                    "color TEXT," +
                    "rotation REAL," +
                    "FOREIGN KEY (room_id) REFERENCES rooms(id))");
            
            // Create designs table to save whole designs
            stmt.execute("CREATE TABLE IF NOT EXISTS designs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "room_id INTEGER," +
                    "user_id INTEGER," +
                    "description TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (room_id) REFERENCES rooms(id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");
            
            // Insert a default admin user (password: admin123)
            // Using BCrypt for password hashing
            stmt.execute("INSERT OR IGNORE INTO users (username, password, full_name, email, role) " +
                    "VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Administrator', 'admin@furniture.com', 'admin')");
            
            // Insert a default designer user (password: designer123)
            stmt.execute("INSERT OR IGNORE INTO users (username, password, full_name, email, role) " +
                    "VALUES ('designer', '$2a$10$xLuG5fFCHM.De0LlAjT7FeZQRWGcwK7NX.CeZ1WJ7Qv695zCGMXpK', 'Designer', 'designer@furniture.com', 'designer')");
                    
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}