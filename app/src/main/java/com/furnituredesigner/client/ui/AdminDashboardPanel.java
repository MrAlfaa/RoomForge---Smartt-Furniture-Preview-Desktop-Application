package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AdminDashboardPanel extends JPanel {
    
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private UserManagementPanel userManagementPanel;
    
    // Remove the designerNavAction parameter from constructor
    public AdminDashboardPanel(User user) {
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        
        // Create card layout for the content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create and configure the navigation panel
        NavigationPanel navPanel = new NavigationPanel(contentPanel, cardLayout);
        
        // Add menu items with simple text icons instead of emoji
        navPanel.addMenuItem("Dashboard", "dashboard", "D");
        navPanel.addMenuItem("Users", "users", "U");
        navPanel.addMenuItem("Projects", "projects", "P");
        navPanel.addMenuItem("Reports", "reports", "R");
        navPanel.addMenuItem("Settings", "settings", "S");
        
        // Remove the separator and Designer Dashboard navigation button
        // navPanel.addSeparator();
        // navPanel.addNavButton("Designer Dashboard", "DD", designerNavAction);
        
        // Create content panels
        JPanel dashboardContent = createDashboardContent();
        
        // Create and add UserManagementPanel
        userManagementPanel = new UserManagementPanel(currentUser);
        
        JPanel projectsContent = createProjectsContent();
        JPanel reportsContent = createReportsContent();
        JPanel settingsContent = createSettingsContent();
        
        // Add content panels to the card layout
        contentPanel.add(dashboardContent, "dashboard");
        contentPanel.add(userManagementPanel, "users");
        contentPanel.add(projectsContent, "projects");
        contentPanel.add(reportsContent, "reports");
        contentPanel.add(settingsContent, "settings");
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Add components to the main panel
        add(navPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Show the dashboard by default
        cardLayout.show(contentPanel, "dashboard");
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel(currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            // Call the logout method from the root frame
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).logout();
            }
        });
        
        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createDashboardContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setOpaque(false);
        
        statsPanel.add(createStatCard("Total Users", "158", new Color(41, 128, 185)));
        statsPanel.add(createStatCard("Active Projects", "47", new Color(39, 174, 96)));
        statsPanel.add(createStatCard("New Designs", "23", new Color(142, 68, 173)));
        statsPanel.add(createStatCard("Pending Reviews", "12", new Color(211, 84, 0)));
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centerPanel.setOpaque(false);
        
        centerPanel.add(createChartPanel("User Activity"));
        centerPanel.add(createChartPanel("Project Status"));
        centerPanel.add(createChartPanel("Storage Usage"));
        centerPanel.add(createChartPanel("System Performance"));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 120, 120));
        
        panel.add(valueLabel, BorderLayout.NORTH);
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createChartPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel chartLabel = new JLabel("Chart Placeholder");
        chartLabel.setHorizontalAlignment(JLabel.CENTER);
        chartLabel.setVerticalAlignment(JLabel.CENTER);
        chartLabel.setPreferredSize(new Dimension(200, 150));
        chartLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chartLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUsersContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create a table for users
        String[] columns = {"ID", "Username", "Full Name", "Email", "Role", "Actions"};
        Object[][] data = {
            {1, "admin", "Administrator", "admin@furniture.com", "admin", "Edit Delete"},
            {2, "designer1", "John Designer", "john@furniture.com", "designer", "Edit Delete"},
            {3, "designer2", "Jane Designer", "jane@furniture.com", "designer", "Edit Delete"},
            {4, "user1", "Bob User", "bob@example.com", "user", "Edit Delete"}
        };
        
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        actionPanel.setOpaque(false);
        
        JButton addButton = new JButton("Add New User");
        actionPanel.add(addButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProjectsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Project Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create a table for projects
        String[] columns = {"ID", "Name", "Owner", "Created Date", "Status", "Actions"};
        Object[][] data = {
            {1, "Modern Living Room", "John Designer", "2023-06-15", "Active", "View Edit Delete"},
            {2, "Minimalist Kitchen", "Jane Designer", "2023-06-20", "In Review", "View Edit Delete"},
            {3, "Office Redesign", "Bob User", "2023-06-25", "Completed", "View Edit Delete"},
            {4, "Bedroom Suite", "John Designer", "2023-07-01", "Draft", "View Edit Delete"}
        };
        
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel reportsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        reportsPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        reportsPanel.setOpaque(false);
        
        reportsPanel.add(createReportCard("User Activity Report", "View and analyze user activity over time"));
        reportsPanel.add(createReportCard("Project Status Report", "Overview of project statuses and completion rates"));
        reportsPanel.add(createReportCard("Resource Usage Report", "Monitor system resource usage and performance"));
        reportsPanel.add(createReportCard("Audit Log", "Security and access audit trail"));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(reportsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportCard(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton generateButton = new JButton("Generate Report");
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        panel.add(generateButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSettingsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel settingsForm = new JPanel(new GridLayout(5, 2, 10, 15));
        settingsForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        settingsForm.setOpaque(false);
        
        settingsForm.add(new JLabel("Database Backup Schedule:"));
        settingsForm.add(new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly"}));
        
        settingsForm.add(new JLabel("Email Notifications:"));
        settingsForm.add(new JCheckBox("Enable email notifications"));
        
        settingsForm.add(new JLabel("Log Level:"));
        settingsForm.add(new JComboBox<>(new String[]{"Info", "Warning", "Error", "Debug"}));
        
        settingsForm.add(new JLabel("Session Timeout (minutes):"));
        settingsForm.add(new JTextField("30"));
        
        JButton saveButton = new JButton("Save Settings");
        JButton resetButton = new JButton("Reset to Defaults");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(settingsForm, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}