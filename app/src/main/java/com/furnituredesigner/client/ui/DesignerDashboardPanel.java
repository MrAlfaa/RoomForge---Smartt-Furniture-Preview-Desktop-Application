package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DesignerDashboardPanel extends JPanel {
    
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Remove the adminNavAction parameter from constructor
    public DesignerDashboardPanel(User user) {
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        
        // Create card layout for the content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create and configure the navigation panel
        NavigationPanel navPanel = new NavigationPanel(contentPanel, cardLayout);
        
        // Add menu items with simple text icons instead of emoji
        navPanel.addMenuItem("Projects", "projects", "P");
        navPanel.addMenuItem("New Design", "new-design", "+");
        navPanel.addMenuItem("3D View", "3d-view", "3D");
        navPanel.addMenuItem("Materials", "materials", "M");
        navPanel.addMenuItem("Templates", "templates", "T");
        navPanel.addMenuItem("Settings", "settings", "S");
        
        // Remove the Admin Dashboard navigation button
        // if (user.isAdmin()) {
        //     navPanel.addSeparator();
        //     navPanel.addNavButton("Admin Dashboard", "A", adminNavAction);
        // }
        
        // Create content panels
        JPanel projectsContent = createProjectsContent();
        JPanel newDesignContent = createNewDesignContent();
        JPanel viewContent = create3DViewContent();
        JPanel materialsContent = createMaterialsContent();
        JPanel templatesContent = createTemplatesContent();
        JPanel settingsContent = createSettingsContent();
        
        // Add content panels to the card layout
        contentPanel.add(projectsContent, "projects");
        contentPanel.add(newDesignContent, "new-design");
        contentPanel.add(viewContent, "3d-view");
        contentPanel.add(materialsContent, "materials");
        contentPanel.add(templatesContent, "templates");
        contentPanel.add(settingsContent, "settings");
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Add components to the main panel
        add(navPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Show the projects by default
        cardLayout.show(contentPanel, "projects");
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        JLabel titleLabel = new JLabel("Designer Dashboard");
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
    
    private JPanel createProjectsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("My Projects");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel projectsGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        projectsGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        projectsGrid.setOpaque(false);
        
        // Add project cards
        projectsGrid.add(createProjectCard("Modern Living Room", "Last edited: 3 days ago"));
        projectsGrid.add(createProjectCard("Kitchen Redesign", "Last edited: 1 week ago"));
        projectsGrid.add(createProjectCard("Office Space", "Last edited: 2 weeks ago"));
        projectsGrid.add(createProjectCard("Master Bedroom", "Last edited: 1 month ago"));
        projectsGrid.add(createNewProjectCard());
        
        JScrollPane scrollPane = new JScrollPane(projectsGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProjectCard(String title, String lastEdited) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        panel.setBackground(Color.WHITE);
        
        // Project thumbnail (placeholder)
        JPanel thumbnailPanel = new JPanel();
        thumbnailPanel.setPreferredSize(new Dimension(0, 150));
        thumbnailPanel.setBackground(new Color(240, 240, 240));
        thumbnailPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        JLabel thumbnailLabel = new JLabel("Project Preview");
        thumbnailLabel.setHorizontalAlignment(JLabel.CENTER);
        thumbnailPanel.add(thumbnailLabel);
        
        // Project info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel dateLabel = new JLabel(lastEdited);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(120, 120, 120));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(dateLabel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        
        JButton editButton = new JButton("Open");
        JButton deleteButton = new JButton("Delete");
        
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(thumbnailPanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createNewProjectCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        panel.setBackground(new Color(248, 248, 248));
        
        JButton newButton = new JButton("+ Create New Project");
        newButton.setFont(new Font("Arial", Font.BOLD, 14));
        newButton.setFocusPainted(false);
        newButton.setBackground(new Color(240, 240, 240));
        
        panel.add(newButton, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(0, 220)); // Match height of project cards
        
        return panel;
    }
    
    private JPanel createNewDesignContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Create New Design");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        formPanel.add(new JLabel("Project Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Room Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<String> roomTypeCombo = new JComboBox<>(new String[] {
            "Living Room", "Bedroom", "Kitchen", "Bathroom", "Office", "Dining Room", "Other"
        });
        formPanel.add(roomTypeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Room Dimensions:"), gbc);
        
        gbc.gridx = 1;
        JPanel dimensionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dimensionsPanel.setOpaque(false);
        JTextField widthField = new JTextField(5);
        JTextField lengthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        dimensionsPanel.add(new JLabel("Width:"));
        dimensionsPanel.add(widthField);
        dimensionsPanel.add(new JLabel("Length:"));
        dimensionsPanel.add(lengthField);
        dimensionsPanel.add(new JLabel("Height:"));
        dimensionsPanel.add(heightField);
        formPanel.add(dimensionsPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Template:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> templateCombo = new JComboBox<>(new String[] {
            "None", "Modern Living Room", "Standard Office", "Master Bedroom", "Kitchen Basic"
        });
        formPanel.add(templateCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(4, 20);
        JScrollPane scrollPane = new JScrollPane(descArea);
        formPanel.add(scrollPane, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        JButton createButton = new JButton("Create Design");
        formPanel.add(createButton, gbc);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel create3DViewContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("3D Design View");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Placeholder for 3D view
        JPanel viewPanel = new JPanel();
        viewPanel.setBackground(new Color(240, 240, 240));
        JLabel placeholderLabel = new JLabel("3D View Will Be Displayed Here");
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        viewPanel.add(placeholderLabel);
        
        // Toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(new JButton("Zoom In"));
        toolbar.add(new JButton("Zoom Out"));
        toolbar.add(new JButton("Rotate"));
        toolbar.add(new JButton("Pan"));
        toolbar.add(new JButton("Reset View"));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(toolbar, BorderLayout.SOUTH);
        panel.add(viewPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMaterialsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Materials Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel materialsGrid = new JPanel(new GridLayout(0, 4, 10, 10));
        materialsGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        materialsGrid.setOpaque(false);
        
        // Add material swatches
        String[] materialNames = {
            "Wood - Oak", "Wood - Walnut", "Wood - Pine", 
            "Fabric - Cotton", "Fabric - Leather", "Fabric - Linen",
            "Metal - Steel", "Metal - Aluminum", "Metal - Brass",
            "Glass - Clear", "Glass - Frosted", "Glass - Tinted",
            "Stone - Marble", "Stone - Granite", "Stone - Concrete"
        };
        
        Color[] materialColors = {
            new Color(194, 150, 90), new Color(113, 76, 50), new Color(222, 184, 135),
            new Color(230, 230, 230), new Color(150, 111, 51), new Color(230, 225, 207),
            new Color(192, 192, 192), new Color(211, 211, 211), new Color(181, 166, 66),
            new Color(200, 200, 255, 150), new Color(220, 220, 255, 180), new Color(130, 180, 255, 150),
            new Color(225, 225, 225), new Color(190, 190, 190), new Color(180, 180, 180)
        };
        
        for (int i = 0; i < materialNames.length; i++) {
            materialsGrid.add(createMaterialSwatch(materialNames[i], materialColors[i]));
        }
        
        JScrollPane scrollPane = new JScrollPane(materialsGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createMaterialSwatch(String name, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.setBackground(Color.WHITE);
        
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(color);
        colorPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(colorPanel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTemplatesContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Design Templates");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel templatesGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        templatesGrid.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        templatesGrid.setOpaque(false);
        
        // Add template cards
        templatesGrid.add(createTemplateCard("Modern Living Room", "Contemporary design with open space concept"));
        templatesGrid.add(createTemplateCard("Office Space", "Productive workspace with ergonomic furniture"));
        templatesGrid.add(createTemplateCard("Master Bedroom", "Luxurious bedroom setup with walk-in closet"));
        templatesGrid.add(createTemplateCard("Kitchen Basic", "Functional kitchen with optimized workflow"));
        
        JScrollPane scrollPane = new JScrollPane(templatesGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTemplateCard(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.setBackground(Color.WHITE);
        
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(0, 180));
        JLabel imageLabel = new JLabel("Template Preview");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        infoPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton useButton = new JButton("Use Template");
        useButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(descLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(useButton);
        
        panel.add(imagePanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSettingsContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Designer Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Profile settings
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        profilePanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField nameField = new JTextField(currentUser.getFullName(), 20);
        profilePanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        profilePanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(currentUser.getEmail(), 20);
        profilePanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        profilePanel.add(new JLabel("Change Password:"), gbc);
        
        gbc.gridx = 1;
        JButton changePasswordButton = new JButton("Change Password");
        profilePanel.add(changePasswordButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton saveProfileButton = new JButton("Save Profile");
        profilePanel.add(saveProfileButton, gbc);
        
        // Preferences settings
        JPanel preferencesPanel = new JPanel(new GridBagLayout());
        preferencesPanel.setBackground(Color.WHITE);
        preferencesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        preferencesPanel.add(new JLabel("Default Units:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<String> unitsCombo = new JComboBox<>(new String[] {"Metric (cm)", "Imperial (inches)"});
        preferencesPanel.add(unitsCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        preferencesPanel.add(new JLabel("Default View:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> viewCombo = new JComboBox<>(new String[] {"2D Top View", "3D Perspective"});
        preferencesPanel.add(viewCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        preferencesPanel.add(new JLabel("Autosave Interval:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> autosaveCombo = new JComboBox<>(new String[] {"1 minute", "5 minutes", "10 minutes", "Never"});
        preferencesPanel.add(autosaveCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton savePrefsButton = new JButton("Save Preferences");
        preferencesPanel.add(savePrefsButton, gbc);
        
        tabbedPane.addTab("Profile", profilePanel);
        tabbedPane.addTab("Preferences", preferencesPanel);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
}