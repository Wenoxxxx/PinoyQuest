package src.core;

import javax.swing.JPanel;
import java.awt.*;
import src.entity.Player;
import src.tile.TileManager;

public class GamePanel extends JPanel {

    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale; // 48x48 pixels
    public final int maxScreenCol = 40; // change from 16
    public final int maxScreenRow = 22; // change from 12

    // Default screen size (used as fallback)
    public final int screenWidth = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px

    public final int maxWorldCol = 100; // example: number of tiles horizontally
    public final int maxWorldRow = 100; // example: number of tiles vertically
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // TILES
    TileManager tileManager = new TileManager(this);
    // KEY INPUT (imported from KeyHandler.java)
    KeyHandler keyHandler = new KeyHandler();

    // PLAYER OBJECT
    public Player player = new Player(this, keyHandler);

    // CAMERA POSITION
    public int cameraX;
    public int cameraY;
    

    // GAME LOOP OBJECT
    private GameLoop gameLoop;

    // CONSTRUCTOR
    public GamePanel() {
        // Don't set preferred size - let it fill the JFrame
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        gameLoop = new GameLoop(this);
    }

    // STARTING GAME BY RUNNING GAME LOOP
    public void startGame() {
        gameLoop.start();
    }

    // UPDATES GAME LOGIC EVERY FRAME [MOVEMENT SPEED]
    public void update() {

        player.update();

        // Player size
        int playerWidth = tileSize * 2;
        int playerHeight = tileSize * 2;

        // Get actual screen size from the panel
        int screenW = getWidth();
        int screenH = getHeight();
        
        // Use default size if panel not sized yet
        if (screenW <= 0) {
            screenW = screenWidth;
        }
        if (screenH <= 0) {
            screenH = screenHeight;
        }

        // Center camera on player
        int targetCameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        int targetCameraY = player.worldY - (screenH / 2) + (playerHeight / 2);

        // Smooth follow (LERP)
        double smoothing = 0.1; // Adjust for speed (0.1 = smoother)
        cameraX += (targetCameraX - cameraX) * smoothing;
        cameraY += (targetCameraY - cameraY) * smoothing;
        
        

    }   

    // DRAWS EVERYTHING OM SCREEN EVERY FRAME
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw Tiles
        tileManager.draw(g2);


        player.draw(g2);

        g2.dispose();

    }

}




