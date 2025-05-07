package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private AdminDashboardPanel adminDashboardPanel;
    private DesignerDashboardPanel designerDashboardPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        
        // Configure the frame
        setTitle("Furniture Designer - Welcome " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create UI components
        setupContentPanel();
        
        // Maximize the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void setupContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create dashboard panels
        adminDashboardPanel = new AdminDashboardPanel(currentUser, e -> switchToDashboard("designer"));
        designerDashboardPanel = new DesignerDashboardPanel(currentUser, e -> switchToDashboard("admin"));
        
        // Add dashboard panels to content panel
        contentPanel.add(adminDashboardPanel, "admin");
        contentPanel.add(designerDashboardPanel, "designer");
        
        // Set default dashboard based on user role
        if (currentUser.isAdmin()) {
            cardLayout.show(contentPanel, "admin");
        } else {
            cardLayout.show(contentPanel, "designer");
        }
        
        // Add content panel to the frame
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void switchToDashboard(String dashboard) {
        cardLayout.show(contentPanel, dashboard);
    }
}