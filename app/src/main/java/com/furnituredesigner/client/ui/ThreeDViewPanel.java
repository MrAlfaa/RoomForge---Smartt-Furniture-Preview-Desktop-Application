package com.furnituredesigner.client.ui;

import com.furnituredesigner.common.model.Room;
import com.furnituredesigner.common.model.Furniture;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.light.DirectionalLight;
import com.jme3.system.awt.AwtPanelsContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ThreeDViewPanel extends JPanel {
    
    private Room room;
    private List<Furniture> furnitureList;
    private JmeCanvasContext context;
    private ThreeDApplication jmeApp;
    
    public ThreeDViewPanel(Room room, List<Furniture> furnitureList) {
        this.room = room;
        this.furnitureList = furnitureList;
        
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Initialize JMonkeyEngine on a separate thread
        initJMonkeyEngine();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("3D Room View: " + room.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        
        JButton backToTwoDButton = new JButton("Back to 2D");
        backToTwoDButton.addActionListener(e -> {
            // Switch back to 2D view
            Container parent = getParent();
            if (parent != null && parent.getLayout() instanceof CardLayout) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "2d-view");
                
                // Clean up JME resources when switching away
                cleanupJMonkeyEngine();
            }
        });
        
        JButton resetViewButton = new JButton("Reset Camera");
        resetViewButton.addActionListener(e -> {
            if (jmeApp != null) {
                jmeApp.resetCamera();
            }
        });
        
        controlPanel.add(resetViewButton);
        controlPanel.add(backToTwoDButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void initJMonkeyEngine() {
        try {
            // Instead of trying to embed JME in the Swing panel,
            // create a standalone window for the 3D view
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setBackground(Color.DARK_GRAY);
            
            JLabel infoLabel = new JLabel("<html><center><h2>3D Preview</h2>" +
                                        "A separate window will open with the 3D view.<br><br>" +
                                        "Close the 3D window to return to this view.</center></html>");
            infoLabel.setForeground(Color.WHITE);
            infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            placeholder.add(infoLabel, BorderLayout.CENTER);
            
            // Button to launch the 3D view
            JButton launchButton = new JButton("Launch 3D View");
            launchButton.addActionListener(e -> {
                launchStandalone3DView();
            });
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.add(launchButton);
            placeholder.add(buttonPanel, BorderLayout.SOUTH);
            
            add(placeholder, BorderLayout.CENTER);
            
            // Add instructions panel
            JPanel instructionsPanel = createInstructionsPanel();
            add(instructionsPanel, BorderLayout.SOUTH);
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Create a fallback panel with error message
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setBackground(Color.DARK_GRAY);
            
            JLabel errorLabel = new JLabel("<html><center><h2>3D Preview Unavailable</h2>" +
                                         "The 3D preview engine could not be initialized.<br><br>" +
                                         "Error: " + e.getMessage() + "<br><br>" +
                                         "You can still use the 2D view for designing your room layout.</center></html>");
            errorLabel.setForeground(Color.WHITE);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JButton backButton = new JButton("Back to 2D View");
            backButton.addActionListener(event -> {
                Container parent = getParent();
                if (parent != null && parent.getLayout() instanceof CardLayout) {
                    CardLayout layout = (CardLayout) parent.getLayout();
                    layout.show(parent, "2d-view");
                }
            });
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);
            buttonPanel.add(backButton);
            
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            errorPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(errorPanel, BorderLayout.CENTER);
        }
    }
    
    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        panel.add(new JLabel("Controls: W/A/S/D - Move | Q/E - Rotate | Mouse Wheel - Zoom | Right-click drag - Look around"));
        
        return panel;
    }
    
    private void cleanupJMonkeyEngine() {
        // Nothing to clean up since we're using a standalone window
        // JME will handle its own cleanup when the window is closed
    }

    private void launchStandalone3DView() {
        try {
            // Launch the 3D application in a separate process
            Thread thread = new Thread(() -> {
                ThreeDStandaloneApplication app = new ThreeDStandaloneApplication(room, furnitureList);
                app.start();
            });
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Failed to launch 3D view: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    // JMonkeyEngine application for 3D rendering
    private class ThreeDApplication extends SimpleApplication {
        private Room room;
        private List<Furniture> furnitureList;
        private Node roomNode;
        private Node furnitureNode;
        
        public ThreeDApplication(Room room, List<Furniture> furnitureList) {
            this.room = room;
            this.furnitureList = furnitureList;
        }
        
        @Override
        public void simpleInitApp() {
            // Set up camera
            flyCam.setMoveSpeed(10f);
            flyCam.setDragToRotate(true);
            
            // Create nodes to organize the scene
            roomNode = new Node("RoomNode");
            furnitureNode = new Node("FurnitureNode");
            rootNode.attachChild(roomNode);
            rootNode.attachChild(furnitureNode);
            
            // Add lighting
            setupLighting();
            
            // Create the room
            createRoom();
            
            // Create furniture
            createFurniture();
            
            // Position camera
            resetCamera();
        }
        
        private void setupLighting() {
            // Add ambient light
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
            sun.setColor(ColorRGBA.White.mult(0.8f));
            rootNode.addLight(sun);
            
            // Add a light from opposite direction
            DirectionalLight fill = new DirectionalLight();
            fill.setDirection(new Vector3f(0.5f, -0.1f, 0.5f).normalizeLocal());
            fill.setColor(ColorRGBA.White.mult(0.6f));
            rootNode.addLight(fill);
        }
        
        private void createRoom() {
            // Convert room color to JME color
            Color roomColor = room.getColorObject();
            ColorRGBA jmeColor = new ColorRGBA(
                roomColor.getRed() / 255f, 
                roomColor.getGreen() / 255f, 
                roomColor.getBlue() / 255f, 
                1.0f
            );
            
            // Create floor and walls based on room shape
            if ("Circular".equals(room.getShape())) {
                createCircularRoom(jmeColor);
            } else {
                createRectangularRoom(jmeColor);
            }
        }
        
        private void createRectangularRoom(ColorRGBA roomColor) {
            float width = (float) room.getWidth();
            float length = (float) room.getLength();
            float height = (float) room.getHeight();
            
            // Create floor
            Box floorBox = new Box(width/2, 0.1f, length/2);
            Geometry floor = new Geometry("Floor", floorBox);
            Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            floorMat.setColor("Color", roomColor.mult(0.8f));
            floor.setMaterial(floorMat);
            floor.setLocalTranslation(0, -0.1f, 0);
            roomNode.attachChild(floor);
            
            // Create walls (using thinner boxes positioned at the edges)
            float wallThickness = 0.1f;
            
            // Wall 1 (back)
            Box wall1Box = new Box(width/2, height/2, wallThickness);
            Geometry wall1 = new Geometry("Wall1", wall1Box);
            Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            wallMat.setColor("Color", roomColor.mult(1.2f));
            wall1.setMaterial(wallMat);
            wall1.setLocalTranslation(0, height/2, -length/2);
            roomNode.attachChild(wall1);
            
            // Wall 2 (front)
            Box wall2Box = new Box(width/2, height/2, wallThickness);
            Geometry wall2 = new Geometry("Wall2", wall2Box);
            wall2.setMaterial(wallMat);
            wall2.setLocalTranslation(0, height/2, length/2);
            roomNode.attachChild(wall2);
            
            // Wall 3 (left)
            Box wall3Box = new Box(wallThickness, height/2, length/2);
            Geometry wall3 = new Geometry("Wall3", wall3Box);
            wall3.setMaterial(wallMat);
            wall3.setLocalTranslation(-width/2, height/2, 0);
            roomNode.attachChild(wall3);
            
            // Wall 4 (right)
            Box wall4Box = new Box(wallThickness, height/2, length/2);
            Geometry wall4 = new Geometry("Wall4", wall4Box);
            wall4.setMaterial(wallMat);
            wall4.setLocalTranslation(width/2, height/2, 0);
            roomNode.attachChild(wall4);
        }
        
        private void createCircularRoom(ColorRGBA roomColor) {
            float radius = (float) room.getWidth() / 2;
            float height = (float) room.getHeight();
            
            // Create cylindrical room (we'll use a cylinder for the walls and a disk for the floor)
            int samples = 32; // Number of segments for the cylinder
            
            // Create floor (using a cylinder with minimal height)
            Cylinder floorCyl = new Cylinder(2, samples, radius, 0.1f, true);
            Geometry floor = new Geometry("Floor", floorCyl);
            Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            floorMat.setColor("Color", roomColor.mult(0.8f));
            floor.setMaterial(floorMat);
            floor.setLocalTranslation(0, -0.05f, 0);
            floor.rotate(90 * FastMath.DEG_TO_RAD, 0, 0); // Rotate to lie flat
            roomNode.attachChild(floor);
            
            // Create walls (using a hollow cylinder)
            Cylinder wallCyl = new Cylinder(2, samples, radius, height, true, false);
            Geometry walls = new Geometry("Walls", wallCyl);
            Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            wallMat.setColor("Color", roomColor.mult(1.2f));
            walls.setMaterial(wallMat);
            walls.setLocalTranslation(0, height/2, 0);
            walls.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
            roomNode.attachChild(walls);
        }
        
        private void createFurniture() {
            // Create 3D models for each furniture item
            for (Furniture furniture : furnitureList) {
                // Convert furniture position (positive y is up in JME)
                float xPos = (float) furniture.getXPos();
                float zPos = (float) furniture.getYPos();
                float yPos = 0; // On the floor
                
                // Get dimensions
                float width = (float) furniture.getWidth();
                float length = (float) furniture.getLength();
                float height = (float) furniture.getHeight();
                
                // Convert rotation (2D rotation around Y axis in 3D)
                float rotationY = (float) (furniture.getRotation() * FastMath.DEG_TO_RAD);
                
                // Convert color
                Color furnitureColor = furniture.getColorObject();
                ColorRGBA jmeColor = new ColorRGBA(
                    furnitureColor.getRed() / 255f,
                    furnitureColor.getGreen() / 255f,
                    furnitureColor.getBlue() / 255f,
                    1.0f
                );
                
                // Create furniture based on type
                Node furnitureItemNode = null;
                
                switch (furniture.getType()) {
                    case "Chair":
                        furnitureItemNode = createChair(width, length, height, jmeColor);
                        break;
                    case "Dining Table":
                        furnitureItemNode = createTable(width, length, height, jmeColor);
                        break;
                    case "Side Table":
                        furnitureItemNode = createTable(width, length, height, jmeColor);
                        break;
                    case "Sofa":
                        furnitureItemNode = createSofa(width, length, height, jmeColor);
                        break;
                    case "Bed":
                        furnitureItemNode = createBed(width, length, height, jmeColor);
                        break;
                    case "Cupboard":
                        furnitureItemNode = createCupboard(width, length, height, jmeColor);
                        break;
                    case "TV Stand":
                        furnitureItemNode = createTVStand(width, length, height, jmeColor);
                        break;
                    case "Bookshelf":
                        furnitureItemNode = createBookshelf(width, length, height, jmeColor);
                        break;
                    default:
                        // Generic box for unknown types
                        furnitureItemNode = new Node(furniture.getType());
                        Box box = new Box(width/2, height/2, length/2);
                        Geometry geo = new Geometry("GenericFurniture", box);
                        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mat.setColor("Color", jmeColor);
                        geo.setMaterial(mat);
                        furnitureItemNode.attachChild(geo);
                }
                
                if (furnitureItemNode != null) {
                    // Position and rotate the furniture
                    furnitureItemNode.setLocalTranslation(xPos, yPos, zPos);
                    furnitureItemNode.rotate(0, rotationY, 0);
                    
                    // Add to scene
                    furnitureNode.attachChild(furnitureItemNode);
                }
            }
        }
        
        private Node createChair(float width, float length, float height, ColorRGBA color) {
            Node chairNode = new Node("Chair");
            
            // Create seat
            Box seatBox = new Box(width/2, height/6, length/2);
            Geometry seat = new Geometry("ChairSeat", seatBox);
            Material seatMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            seatMat.setColor("Color", color);
            seat.setMaterial(seatMat);
            seat.setLocalTranslation(0, height/6, 0);
            chairNode.attachChild(seat);
            
            // Create backrest
            Box backrestBox = new Box(width/2, height/3, length/8);
            Geometry backrest = new Geometry("ChairBackrest", backrestBox);
            Material backrestMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            backrestMat.setColor("Color", color.mult(0.8f));
            backrest.setMaterial(backrestMat);
            backrest.setLocalTranslation(0, height/2, -length/2 + length/16);
            chairNode.attachChild(backrest);
            
            // Create legs
            float legSize = Math.min(width, length) / 10;
            float legHeight = height/3;
            
            Box legBox = new Box(legSize/2, legHeight/2, legSize/2);
            Material legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            legMat.setColor("Color", color.mult(0.7f));
            
            // Front-left leg
            Geometry leg1 = new Geometry("Leg1", legBox);
            leg1.setMaterial(legMat);
            leg1.setLocalTranslation(-width/2 + legSize/2, legHeight/2, length/2 - legSize/2);
            chairNode.attachChild(leg1);
            
            // Front-right leg
            Geometry leg2 = new Geometry("Leg2", legBox);
            leg2.setMaterial(legMat);
            leg2.setLocalTranslation(width/2 - legSize/2, legHeight/2, length/2 - legSize/2);
            chairNode.attachChild(leg2);
            
            // Back-left leg
            Geometry leg3 = new Geometry("Leg3", legBox);
            leg3.setMaterial(legMat);
            leg3.setLocalTranslation(-width/2 + legSize/2, legHeight/2, -length/2 + legSize/2);
            chairNode.attachChild(leg3);
            
            // Back-right leg
            Geometry leg4 = new Geometry("Leg4", legBox);
            leg4.setMaterial(legMat);
            leg4.setLocalTranslation(width/2 - legSize/2, legHeight/2, -length/2 + legSize/2);
            chairNode.attachChild(leg4);
            
            return chairNode;
        }
        
        private Node createTable(float width, float length, float height, ColorRGBA color) {
            Node tableNode = new Node("Table");
            
            // Create table top
            Box topBox = new Box(width/2, height/10, length/2);
            Geometry top = new Geometry("TableTop", topBox);
            Material topMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            topMat.setColor("Color", color);
            top.setMaterial(topMat);
            top.setLocalTranslation(0, height - height/10, 0);
            tableNode.attachChild(top);
            
            // Create legs
            float legSize = Math.min(width, length) / 10;
            
            Box legBox = new Box(legSize/2, height/2, legSize/2);
            Material legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            legMat.setColor("Color", color.mult(0.7f));
            
            // Front-left leg
            Geometry leg1 = new Geometry("Leg1", legBox);
            leg1.setMaterial(legMat);
            leg1.setLocalTranslation(-width/2 + legSize/2, height/2, length/2 - legSize/2);
            tableNode.attachChild(leg1);
            
            // Front-right leg
            Geometry leg2 = new Geometry("Leg2", legBox);
            leg2.setMaterial(legMat);
            leg2.setLocalTranslation(width/2 - legSize/2, height/2, length/2 - legSize/2);
            tableNode.attachChild(leg2);
            
            // Back-left leg
            Geometry leg3 = new Geometry("Leg3", legBox);
            leg3.setMaterial(legMat);
            leg3.setLocalTranslation(-width/2 + legSize/2, height/2, -length/2 + legSize/2);
            tableNode.attachChild(leg3);
            
            // Back-right leg
            Geometry leg4 = new Geometry("Leg4", legBox);
            leg4.setMaterial(legMat);
            leg4.setLocalTranslation(width/2 - legSize/2, height/2, -length/2 + legSize/2);
            tableNode.attachChild(leg4);
            
            return tableNode;
        }
        
        private Node createSofa(float width, float length, float height, ColorRGBA color) {
            Node sofaNode = new Node("Sofa");
            
            // Create seat
            Box seatBox = new Box(width/2, height/3, length/2);
            Geometry seat = new Geometry("SofaSeat", seatBox);
            Material seatMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            seatMat.setColor("Color", color);
            seat.setMaterial(seatMat);
            seat.setLocalTranslation(0, height/3, 0);
            sofaNode.attachChild(seat);
            
            // Create backrest
            Box backrestBox = new Box(width/2, height/3, length/8);
            Geometry backrest = new Geometry("SofaBackrest", backrestBox);
            Material backrestMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            backrestMat.setColor("Color", color.mult(0.8f));
            backrest.setMaterial(backrestMat);
            backrest.setLocalTranslation(0, height/3 + height/3, -length/2 + length/16);
            sofaNode.attachChild(backrest);
            
            // Create armrests
            float armWidth = width/10;
            
            Box armBox = new Box(armWidth/2, height/3, length/2);
            Material armMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            armMat.setColor("Color", color.mult(0.8f));
            
            // Left arm
            Geometry leftArm = new Geometry("LeftArm", armBox);
            leftArm.setMaterial(armMat);
            leftArm.setLocalTranslation(-width/2 - armWidth/2, height/3, 0);
            sofaNode.attachChild(leftArm);
            
            // Right arm
            Geometry rightArm = new Geometry("RightArm", armBox);
            rightArm.setMaterial(armMat);
            rightArm.setLocalTranslation(width/2 + armWidth/2, height/3, 0);
            sofaNode.attachChild(rightArm);
            
            return sofaNode;
        }
        
        private Node createBed(float width, float length, float height, ColorRGBA color) {
            Node bedNode = new Node("Bed");
            
            // Create mattress
            Box mattressBox = new Box(width/2, height/3, length/2);
            Geometry mattress = new Geometry("Mattress", mattressBox);
            Material mattressMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mattressMat.setColor("Color", color);
            mattress.setMaterial(mattressMat);
            mattress.setLocalTranslation(0, height/3, 0);
            bedNode.attachChild(mattress);
            
            // Create bed frame
            Box frameBox = new Box(width/2 + width/20, height/6, length/2 + length/20);
            Geometry frame = new Geometry("BedFrame", frameBox);
            Material frameMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            frameMat.setColor("Color", color.mult(0.7f));
            frame.setMaterial(frameMat);
            frame.setLocalTranslation(0, height/6, 0);
            bedNode.attachChild(frame);
            
            // Create headboard
            float headboardHeight = height/2;
            Box headboardBox = new Box(width/2, headboardHeight/2, length/16);
            Geometry headboard = new Geometry("Headboard", headboardBox);
            Material headboardMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            headboardMat.setColor("Color", color.mult(0.7f));
            headboard.setMaterial(headboardMat);
            headboard.setLocalTranslation(0, headboardHeight/2 + height/6, -length/2 - length/16);
            bedNode.attachChild(headboard);
            
            return bedNode;
        }
        
        private Node createCupboard(float width, float length, float height, ColorRGBA color) {
            Node cupboardNode = new Node("Cupboard");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("CupboardBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            cupboardNode.attachChild(body);
            
            // Add door lines
            Material lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            lineMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Vertical divider
            Box dividerBox = new Box(0.01f, height/2, 0.01f);
            Geometry divider = new Geometry("Divider", dividerBox);
            divider.setMaterial(lineMat);
            divider.setLocalTranslation(0, height/2, length/2 + 0.01f);
            cupboardNode.attachChild(divider);
            
            // Handles
            float handleSize = Math.min(width, length) / 15;
            Box handleBox = new Box(handleSize/2, handleSize/2, handleSize/2);
            Material handleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            handleMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Left handle
            Geometry leftHandle = new Geometry("LeftHandle", handleBox);
            leftHandle.setMaterial(handleMat);
            leftHandle.setLocalTranslation(-width/4, height/2, length/2 + handleSize/2);
            cupboardNode.attachChild(leftHandle);
            
            // Right handle
            Geometry rightHandle = new Geometry("RightHandle", handleBox);
            rightHandle.setMaterial(handleMat);
            rightHandle.setLocalTranslation(width/4, height/2, length/2 + handleSize/2);
            cupboardNode.attachChild(rightHandle);
            
            return cupboardNode;
        }
        
        private Node createTVStand(float width, float length, float height, ColorRGBA color) {
            Node tvStandNode = new Node("TVStand");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("TVStandBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            tvStandNode.attachChild(body);
            
            // Add shelf line
            Material lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            lineMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Middle shelf
            Box shelfBox = new Box(width/2, 0.01f, length/2);
            Geometry shelf = new Geometry("Shelf", shelfBox);
            shelf.setMaterial(lineMat);
            shelf.setLocalTranslation(0, height/2, 0);
            tvStandNode.attachChild(shelf);
            
            return tvStandNode;
        }
        
        private Node createBookshelf(float width, float length, float height, ColorRGBA color) {
            Node bookshelfNode = new Node("Bookshelf");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("BookshelfBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            bookshelfNode.attachChild(body);
            
            // Add shelves
            Material shelfMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            shelfMat.setColor("Color", ColorRGBA.DarkGray);
            
            int numShelves = 4;
            float shelfSpacing = height / (numShelves + 1);
            
            for (int i = 1; i <= numShelves; i++) {
                float y = i * shelfSpacing;
                
                Box shelfBox = new Box(width/2, 0.01f, length/2);
                Geometry shelf = new Geometry("Shelf" + i, shelfBox);
                shelf.setMaterial(shelfMat);
                shelf.setLocalTranslation(0, y, 0);
                bookshelfNode.attachChild(shelf);
            }
            
            return bookshelfNode;
        }
        
        public void resetCamera() {
            // Calculate where to position the camera based on room size
            float distance = (float) Math.max(room.getWidth(), room.getLength()) * 1.5f;
            float height = (float) room.getHeight() * 0.8f;
            
            // Position camera at an angle looking into the room
            cam.setLocation(new Vector3f(distance, height, distance));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        }
        
        @Override
        public void simpleUpdate(float tpf) {
            // Nothing to do here for this application
        }
    }
    
    private class ThreeDStandaloneApplication extends SimpleApplication {
        private Room room;
        private List<Furniture> furnitureList;
        private Node roomNode;
        private Node furnitureNode;
        
        public ThreeDStandaloneApplication(Room room, List<Furniture> furnitureList) {
            this.room = room;
            this.furnitureList = furnitureList;
            
            // Configure appropriate settings for standalone mode
            AppSettings settings = new AppSettings(true);
            settings.setTitle("RoomForge - 3D View: " + room.getName());
            settings.setWidth(1024);
            settings.setHeight(768);
            // Don't specify renderer - let JME choose the best one
            setSettings(settings);
            setShowSettings(false); // Skip the settings dialog
        }
        
        @Override
        public void simpleInitApp() {
            // Set up camera
            flyCam.setMoveSpeed(10f);
            
            // Create nodes to organize the scene
            roomNode = new Node("RoomNode");
            furnitureNode = new Node("FurnitureNode");
            rootNode.attachChild(roomNode);
            rootNode.attachChild(furnitureNode);
            
            // Add lighting
            setupLighting();
            
            // Create the room
            createRoom();
            
            // Create furniture
            createFurniture();
            
            // Position camera
            resetCamera();
        }
        
        private void setupLighting() {
            // Add ambient light
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
            sun.setColor(ColorRGBA.White.mult(0.8f));
            rootNode.addLight(sun);
            
            // Add a light from opposite direction
            DirectionalLight fill = new DirectionalLight();
            fill.setDirection(new Vector3f(0.5f, -0.1f, 0.5f).normalizeLocal());
            fill.setColor(ColorRGBA.White.mult(0.6f));
            rootNode.addLight(fill);
        }
        
        private void createRoom() {
            // Convert room color to JME color
            Color roomColor = room.getColorObject();
            ColorRGBA jmeColor = new ColorRGBA(
                roomColor.getRed() / 255f, 
                roomColor.getGreen() / 255f, 
                roomColor.getBlue() / 255f, 
                1.0f
            );
            
            // Create floor and walls based on room shape
            if ("Circular".equals(room.getShape())) {
                createCircularRoom(jmeColor);
            } else {
                createRectangularRoom(jmeColor);
            }
        }
        
        private void createRectangularRoom(ColorRGBA roomColor) {
            float width = (float) room.getWidth();
            float length = (float) room.getLength();
            float height = (float) room.getHeight();
            
            // Create floor
            Box floorBox = new Box(width/2, 0.1f, length/2);
            Geometry floor = new Geometry("Floor", floorBox);
            Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            floorMat.setColor("Color", roomColor.mult(0.8f));
            floor.setMaterial(floorMat);
            floor.setLocalTranslation(0, -0.1f, 0);
            roomNode.attachChild(floor);
            
            // Create walls (using thinner boxes positioned at the edges)
            float wallThickness = 0.1f;
            
            // Wall 1 (back)
            Box wall1Box = new Box(width/2, height/2, wallThickness);
            Geometry wall1 = new Geometry("Wall1", wall1Box);
            Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            wallMat.setColor("Color", roomColor.mult(1.2f));
            wall1.setMaterial(wallMat);
            wall1.setLocalTranslation(0, height/2, -length/2);
            roomNode.attachChild(wall1);
            
            // Wall 2 (front)
            Box wall2Box = new Box(width/2, height/2, wallThickness);
            Geometry wall2 = new Geometry("Wall2", wall2Box);
            wall2.setMaterial(wallMat);
            wall2.setLocalTranslation(0, height/2, length/2);
            roomNode.attachChild(wall2);
            
            // Wall 3 (left)
            Box wall3Box = new Box(wallThickness, height/2, length/2);
            Geometry wall3 = new Geometry("Wall3", wall3Box);
            wall3.setMaterial(wallMat);
            wall3.setLocalTranslation(-width/2, height/2, 0);
            roomNode.attachChild(wall3);
            
            // Wall 4 (right)
            Box wall4Box = new Box(wallThickness, height/2, length/2);
            Geometry wall4 = new Geometry("Wall4", wall4Box);
            wall4.setMaterial(wallMat);
            wall4.setLocalTranslation(width/2, height/2, 0);
            roomNode.attachChild(wall4);
        }
        
        private void createCircularRoom(ColorRGBA roomColor) {
            float radius = (float) room.getWidth() / 2;
            float height = (float) room.getHeight();
            
            // Create cylindrical room (we'll use a cylinder for the walls and a disk for the floor)
            int samples = 32; // Number of segments for the cylinder
            
            // Create floor (using a cylinder with minimal height)
            Cylinder floorCyl = new Cylinder(2, samples, radius, 0.1f, true);
            Geometry floor = new Geometry("Floor", floorCyl);
            Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            floorMat.setColor("Color", roomColor.mult(0.8f));
            floor.setMaterial(floorMat);
            floor.setLocalTranslation(0, -0.05f, 0);
            floor.rotate(90 * FastMath.DEG_TO_RAD, 0, 0); // Rotate to lie flat
            roomNode.attachChild(floor);
            
            // Create walls (using a hollow cylinder)
            Cylinder wallCyl = new Cylinder(2, samples, radius, height, true, false);
            Geometry walls = new Geometry("Walls", wallCyl);
            Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            wallMat.setColor("Color", roomColor.mult(1.2f));
            walls.setMaterial(wallMat);
            walls.setLocalTranslation(0, height/2, 0);
            walls.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
            roomNode.attachChild(walls);
        }
        
        private void createFurniture() {
            // Create 3D models for each furniture item
            for (Furniture furniture : furnitureList) {
                // Convert furniture position (positive y is up in JME)
                float xPos = (float) furniture.getXPos();
                float zPos = (float) furniture.getYPos();
                float yPos = 0; // On the floor
                
                // Get dimensions
                float width = (float) furniture.getWidth();
                float length = (float) furniture.getLength();
                float height = (float) furniture.getHeight();
                
                // Convert rotation (2D rotation around Y axis in 3D)
                float rotationY = (float) (furniture.getRotation() * FastMath.DEG_TO_RAD);
                
                // Convert color
                Color furnitureColor = furniture.getColorObject();
                ColorRGBA jmeColor = new ColorRGBA(
                    furnitureColor.getRed() / 255f,
                    furnitureColor.getGreen() / 255f,
                    furnitureColor.getBlue() / 255f,
                    1.0f
                );
                
                // Create furniture based on type
                Node furnitureItemNode = null;
                
                switch (furniture.getType()) {
                    case "Chair":
                        furnitureItemNode = createChair(width, length, height, jmeColor);
                        break;
                    case "Dining Table":
                        furnitureItemNode = createTable(width, length, height, jmeColor);
                        break;
                    case "Side Table":
                        furnitureItemNode = createTable(width, length, height, jmeColor);
                        break;
                    case "Sofa":
                        furnitureItemNode = createSofa(width, length, height, jmeColor);
                        break;
                    case "Bed":
                        furnitureItemNode = createBed(width, length, height, jmeColor);
                        break;
                    case "Cupboard":
                        furnitureItemNode = createCupboard(width, length, height, jmeColor);
                        break;
                    case "TV Stand":
                        furnitureItemNode = createTVStand(width, length, height, jmeColor);
                        break;
                    case "Bookshelf":
                        furnitureItemNode = createBookshelf(width, length, height, jmeColor);
                        break;
                    default:
                        // Generic box for unknown types
                        furnitureItemNode = new Node(furniture.getType());
                        Box box = new Box(width/2, height/2, length/2);
                        Geometry geo = new Geometry("GenericFurniture", box);
                        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mat.setColor("Color", jmeColor);
                        geo.setMaterial(mat);
                        furnitureItemNode.attachChild(geo);
                }
                
                if (furnitureItemNode != null) {
                    // Position and rotate the furniture
                    furnitureItemNode.setLocalTranslation(xPos, yPos, zPos);
                    furnitureItemNode.rotate(0, rotationY, 0);
                    
                    // Add to scene
                    furnitureNode.attachChild(furnitureItemNode);
                }
            }
        }
        
        private Node createChair(float width, float length, float height, ColorRGBA color) {
            Node chairNode = new Node("Chair");
            
            // Create seat
            Box seatBox = new Box(width/2, height/6, length/2);
            Geometry seat = new Geometry("ChairSeat", seatBox);
            Material seatMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            seatMat.setColor("Color", color);
            seat.setMaterial(seatMat);
            seat.setLocalTranslation(0, height/6, 0);
            chairNode.attachChild(seat);
            
            // Create backrest
            Box backrestBox = new Box(width/2, height/3, length/8);
            Geometry backrest = new Geometry("ChairBackrest", backrestBox);
            Material backrestMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            backrestMat.setColor("Color", color.mult(0.8f));
            backrest.setMaterial(backrestMat);
            backrest.setLocalTranslation(0, height/2, -length/2 + length/16);
            chairNode.attachChild(backrest);
            
            // Create legs
            float legSize = Math.min(width, length) / 10;
            float legHeight = height/3;
            
            Box legBox = new Box(legSize/2, legHeight/2, legSize/2);
            Material legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            legMat.setColor("Color", color.mult(0.7f));
            
            // Front-left leg
            Geometry leg1 = new Geometry("Leg1", legBox);
            leg1.setMaterial(legMat);
            leg1.setLocalTranslation(-width/2 + legSize/2, legHeight/2, length/2 - legSize/2);
            chairNode.attachChild(leg1);
            
            // Front-right leg
            Geometry leg2 = new Geometry("Leg2", legBox);
            leg2.setMaterial(legMat);
            leg2.setLocalTranslation(width/2 - legSize/2, legHeight/2, length/2 - legSize/2);
            chairNode.attachChild(leg2);
            
            // Back-left leg
            Geometry leg3 = new Geometry("Leg3", legBox);
            leg3.setMaterial(legMat);
            leg3.setLocalTranslation(-width/2 + legSize/2, legHeight/2, -length/2 + legSize/2);
            chairNode.attachChild(leg3);
            
            // Back-right leg
            Geometry leg4 = new Geometry("Leg4", legBox);
            leg4.setMaterial(legMat);
            leg4.setLocalTranslation(width/2 - legSize/2, legHeight/2, -length/2 + legSize/2);
            chairNode.attachChild(leg4);
            
            return chairNode;
        }
        
        private Node createTable(float width, float length, float height, ColorRGBA color) {
            Node tableNode = new Node("Table");
            
            // Create table top
            Box topBox = new Box(width/2, height/10, length/2);
            Geometry top = new Geometry("TableTop", topBox);
            Material topMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            topMat.setColor("Color", color);
            top.setMaterial(topMat);
            top.setLocalTranslation(0, height - height/10, 0);
            tableNode.attachChild(top);
            
            // Create legs
            float legSize = Math.min(width, length) / 10;
            
            Box legBox = new Box(legSize/2, height/2, legSize/2);
            Material legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            legMat.setColor("Color", color.mult(0.7f));
            
            // Front-left leg
            Geometry leg1 = new Geometry("Leg1", legBox);
            leg1.setMaterial(legMat);
            leg1.setLocalTranslation(-width/2 + legSize/2, height/2, length/2 - legSize/2);
            tableNode.attachChild(leg1);
            
            // Front-right leg
            Geometry leg2 = new Geometry("Leg2", legBox);
            leg2.setMaterial(legMat);
            leg2.setLocalTranslation(width/2 - legSize/2, height/2, length/2 - legSize/2);
            tableNode.attachChild(leg2);
            
            // Back-left leg
            Geometry leg3 = new Geometry("Leg3", legBox);
            leg3.setMaterial(legMat);
            leg3.setLocalTranslation(-width/2 + legSize/2, height/2, -length/2 + legSize/2);
            tableNode.attachChild(leg3);
            
            // Back-right leg
            Geometry leg4 = new Geometry("Leg4", legBox);
            leg4.setMaterial(legMat);
            leg4.setLocalTranslation(width/2 - legSize/2, height/2, -length/2 + legSize/2);
            tableNode.attachChild(leg4);
            
            return tableNode;
        }
        
        private Node createSofa(float width, float length, float height, ColorRGBA color) {
            Node sofaNode = new Node("Sofa");
            
            // Create seat
            Box seatBox = new Box(width/2, height/3, length/2);
            Geometry seat = new Geometry("SofaSeat", seatBox);
            Material seatMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            seatMat.setColor("Color", color);
            seat.setMaterial(seatMat);
            seat.setLocalTranslation(0, height/3, 0);
            sofaNode.attachChild(seat);
            
            // Create backrest
            Box backrestBox = new Box(width/2, height/3, length/8);
            Geometry backrest = new Geometry("SofaBackrest", backrestBox);
            Material backrestMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            backrestMat.setColor("Color", color.mult(0.8f));
            backrest.setMaterial(backrestMat);
            backrest.setLocalTranslation(0, height/3 + height/3, -length/2 + length/16);
            sofaNode.attachChild(backrest);
            
            // Create armrests
            float armWidth = width/10;
            
            Box armBox = new Box(armWidth/2, height/3, length/2);
            Material armMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            armMat.setColor("Color", color.mult(0.8f));
            
            // Left arm
            Geometry leftArm = new Geometry("LeftArm", armBox);
            leftArm.setMaterial(armMat);
            leftArm.setLocalTranslation(-width/2 - armWidth/2, height/3, 0);
            sofaNode.attachChild(leftArm);
            
            // Right arm
            Geometry rightArm = new Geometry("RightArm", armBox);
            rightArm.setMaterial(armMat);
            rightArm.setLocalTranslation(width/2 + armWidth/2, height/3, 0);
            sofaNode.attachChild(rightArm);
            
            return sofaNode;
        }
        
        private Node createBed(float width, float length, float height, ColorRGBA color) {
            Node bedNode = new Node("Bed");
            
            // Create mattress
            Box mattressBox = new Box(width/2, height/3, length/2);
            Geometry mattress = new Geometry("Mattress", mattressBox);
            Material mattressMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mattressMat.setColor("Color", color);
            mattress.setMaterial(mattressMat);
            mattress.setLocalTranslation(0, height/3, 0);
            bedNode.attachChild(mattress);
            
            // Create bed frame
            Box frameBox = new Box(width/2 + width/20, height/6, length/2 + length/20);
            Geometry frame = new Geometry("BedFrame", frameBox);
            Material frameMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            frameMat.setColor("Color", color.mult(0.7f));
            frame.setMaterial(frameMat);
            frame.setLocalTranslation(0, height/6, 0);
            bedNode.attachChild(frame);
            
            // Create headboard
            float headboardHeight = height/2;
            Box headboardBox = new Box(width/2, headboardHeight/2, length/16);
            Geometry headboard = new Geometry("Headboard", headboardBox);
            Material headboardMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            headboardMat.setColor("Color", color.mult(0.7f));
            headboard.setMaterial(headboardMat);
            headboard.setLocalTranslation(0, headboardHeight/2 + height/6, -length/2 - length/16);
            bedNode.attachChild(headboard);
            
            return bedNode;
        }
        
        private Node createCupboard(float width, float length, float height, ColorRGBA color) {
            Node cupboardNode = new Node("Cupboard");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("CupboardBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            cupboardNode.attachChild(body);
            
            // Add door lines
            Material lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            lineMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Vertical divider
            Box dividerBox = new Box(0.01f, height/2, 0.01f);
            Geometry divider = new Geometry("Divider", dividerBox);
            divider.setMaterial(lineMat);
            divider.setLocalTranslation(0, height/2, length/2 + 0.01f);
            cupboardNode.attachChild(divider);
            
            // Handles
            float handleSize = Math.min(width, length) / 15;
            Box handleBox = new Box(handleSize/2, handleSize/2, handleSize/2);
            Material handleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            handleMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Left handle
            Geometry leftHandle = new Geometry("LeftHandle", handleBox);
            leftHandle.setMaterial(handleMat);
            leftHandle.setLocalTranslation(-width/4, height/2, length/2 + handleSize/2);
            cupboardNode.attachChild(leftHandle);
            
            // Right handle
            Geometry rightHandle = new Geometry("RightHandle", handleBox);
            rightHandle.setMaterial(handleMat);
            rightHandle.setLocalTranslation(width/4, height/2, length/2 + handleSize/2);
            cupboardNode.attachChild(rightHandle);
            
            return cupboardNode;
        }
        
        private Node createTVStand(float width, float length, float height, ColorRGBA color) {
            Node tvStandNode = new Node("TVStand");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("TVStandBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            tvStandNode.attachChild(body);
            
            // Add shelf line
            Material lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            lineMat.setColor("Color", ColorRGBA.DarkGray);
            
            // Middle shelf
            Box shelfBox = new Box(width/2, 0.01f, length/2);
            Geometry shelf = new Geometry("Shelf", shelfBox);
            shelf.setMaterial(lineMat);
            shelf.setLocalTranslation(0, height/2, 0);
            tvStandNode.attachChild(shelf);
            
            return tvStandNode;
        }
        
        private Node createBookshelf(float width, float length, float height, ColorRGBA color) {
            Node bookshelfNode = new Node("Bookshelf");
            
            // Create main body
            Box bodyBox = new Box(width/2, height/2, length/2);
            Geometry body = new Geometry("BookshelfBody", bodyBox);
            Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bodyMat.setColor("Color", color);
            body.setMaterial(bodyMat);
            body.setLocalTranslation(0, height/2, 0);
            bookshelfNode.attachChild(body);
            
            // Add shelves
            Material shelfMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            shelfMat.setColor("Color", ColorRGBA.DarkGray);
            
            int numShelves = 4;
            float shelfSpacing = height / (numShelves + 1);
            
            for (int i = 1; i <= numShelves; i++) {
                float y = i * shelfSpacing;
                
                Box shelfBox = new Box(width/2, 0.01f, length/2);
                Geometry shelf = new Geometry("Shelf" + i, shelfBox);
                shelf.setMaterial(shelfMat);
                shelf.setLocalTranslation(0, y, 0);
                bookshelfNode.attachChild(shelf);
            }
            
            return bookshelfNode;
        }
        
        public void resetCamera() {
            // Calculate where to position the camera based on room size
            float distance = (float) Math.max(room.getWidth(), room.getLength()) * 1.5f;
            float height = (float) room.getHeight() * 0.8f;
            
            // Position camera at an angle looking into the room
            cam.setLocation(new Vector3f(distance, height, distance));
            cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        }
        
        @Override
        public void simpleUpdate(float tpf) {
            // Nothing to do here for this application
        }
    }
}