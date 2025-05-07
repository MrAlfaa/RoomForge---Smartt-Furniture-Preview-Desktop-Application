package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class TwoDViewPanel extends JPanel {
    
    private Room room;
    private JPanel drawingPanel;
    private double scale = 50.0; // 50 pixels per meter
    private Point dragStart;
    private Point viewOffset = new Point(0, 0);
    
    public TwoDViewPanel(Room room) {
        this.room = room;
        setLayout(new BorderLayout());
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("2D Room View: " + room.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");
        JButton resetViewButton = new JButton("Reset View");
        
        zoomInButton.addActionListener(e -> {
            scale *= 1.2;
            drawingPanel.repaint();
        });
        
        zoomOutButton.addActionListener(e -> {
            scale /= 1.2;
            drawingPanel.repaint();
        });
        
        resetViewButton.addActionListener(e -> {
            scale = 50.0;
            viewOffset = new Point(0, 0);
            drawingPanel.repaint();
        });
        
        controlPanel.add(zoomInButton);
        controlPanel.add(zoomOutButton);
        controlPanel.add(resetViewButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        // Create drawing panel for 2D view
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRoom(g);
            }
        };
        drawingPanel.setBackground(Color.WHITE);
        
        // Enable panning by dragging
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
            }
        });
        
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    viewOffset.x += dx;
                    viewOffset.y += dy;
                    dragStart = e.getPoint();
                    drawingPanel.repaint();
                }
            }
        });
        
        // Add mouse wheel support for zooming
        drawingPanel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                // Zoom in
                scale *= 1.1;
            } else {
                // Zoom out
                scale /= 1.1;
            }
            drawingPanel.repaint();
        });
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        infoPanel.setBackground(new Color(245, 245, 245));
        
        infoPanel.add(new JLabel("Shape: " + room.getShape()));
        if ("Rectangle".equals(room.getShape()) || "Square".equals(room.getShape())) {
            infoPanel.add(new JLabel(String.format("Width: %.2f m", room.getWidth())));
            infoPanel.add(new JLabel(String.format("Length: %.2f m", room.getLength())));
        } else {
            infoPanel.add(new JLabel(String.format("Diameter: %.2f m", room.getWidth())));
        }
        infoPanel.add(new JLabel(String.format("Height: %.2f m", room.getHeight())));
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void drawRoom(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Calculate center of panel
        int centerX = drawingPanel.getWidth() / 2 + viewOffset.x;
        int centerY = drawingPanel.getHeight() / 2 + viewOffset.y;
        
        // Set color based on room color
        Color roomColor = room.getColorObject();
        Color wallColor = roomColor.darker();
        
        // Draw room based on shape
        if ("Circular".equals(room.getShape())) {
            // Draw circular room
            int radius = (int) (room.getWidth() * scale / 2);
            
            // Draw floor
            g2d.setColor(roomColor);
            g2d.fill(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2));
            
            // Draw walls (outline)
            g2d.setColor(wallColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2, radius * 2));
            
        } else {
            // Draw rectangular or square room
            int width = (int) (room.getWidth() * scale);
            int length = (int) (room.getLength() * scale);
            
            // Draw floor
            g2d.setColor(roomColor);
            g2d.fill(new Rectangle2D.Double(centerX - width / 2, centerY - length / 2, width, length));
            
            // Draw walls (outline)
            g2d.setColor(wallColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(new Rectangle2D.Double(centerX - width / 2, centerY - length / 2, width, length));
        }
        
        // Draw coordinate system
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
        
        // Draw horizontal gridlines every meter
        for (int i = -20; i <= 20; i++) {
            int y = centerY + (int)(i * scale);
            g2d.drawLine(0, y, drawingPanel.getWidth(), y);
        }
        
        // Draw vertical gridlines every meter
        for (int i = -20; i <= 20; i++) {
            int x = centerX + (int)(i * scale);
            g2d.drawLine(x, 0, x, drawingPanel.getHeight());
        }
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(centerX, 0, centerX, drawingPanel.getHeight()); // Y axis
        g2d.drawLine(0, centerY, drawingPanel.getWidth(), centerY); // X axis
        
        // Draw legend
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Scale: 1m = " + (int)scale + "px", 20, 20);
    }
    
    public void setRoom(Room room) {
        this.room = room;
        repaint();
    }
}