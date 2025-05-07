package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.User;
import com.furnituredesigner.server.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginPanel extends JPanel {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private LoginListener loginListener;
    
    public interface LoginListener {
        void onLoginSuccess(User user);
    }
    
    public LoginPanel(LoginListener listener) {
        this.loginListener = listener;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Furniture Designer Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        
        // Add components to panels
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(cancelButton);
        formPanel.add(loginButton);
        
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        // Add action listeners
        loginButton.addActionListener(this::attemptLogin);
        cancelButton.addActionListener(e -> System.exit(0));
        
        // Set default values for testing
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }
    
    private void attemptLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // For quick testing - bypass authentication
        if (true) { // Remove this after testing
            User testUser = new User(1, username, "Test User", "test@example.com");
            loginListener.onLoginSuccess(testUser);
            return;
        }
        
        // Attempt to authenticate
        try {
            AuthService authService = new AuthService();
            User user = authService.authenticate(username, password);
            
            if (user != null) {
                // Successful login
                if (loginListener != null) {
                    loginListener.onLoginSuccess(user);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Login error: " + ex.getMessage(), 
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}