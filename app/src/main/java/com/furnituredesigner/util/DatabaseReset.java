package com.furnituredesigner.util;

import java.io.File;

public class DatabaseReset {
    
    public static void main(String[] args) {
        System.out.println("Attempting to reset the database...");
        
        String dbDirectory = "database";
        String dbFile = dbDirectory + File.separator + "furniture_designer.db";
        
        File file = new File(dbFile);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Database file successfully deleted.");
                System.out.println("The application will create a new database on next startup.");
            } else {
                System.out.println("Failed to delete database file. Make sure the application is not running.");
            }
        } else {
            System.out.println("Database file does not exist at: " + file.getAbsolutePath());
        }
    }
}