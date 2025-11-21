package src.core;

import javax.swing.JPanel;
import java.awt.*;

import src.entity.Player;
import src.tile.ObjectManager;
import src.tile.TileManager;

public class GamePanel extends JPanel {

    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale; // 48x48 pixels
    public final int maxScreenCol = 40; // change from 16
    public final int maxScreenRow = 22; // change from 12

    // Default screen size (used as fallback)
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // MAP WORLD SIZE --------------------------------------------------------------
    public final int maxWorldCol = 31; // WIDTH  | number of tiles horizontally
    public final int maxWorldRow = 21; // HEIGHT | number of tiles vertically
    // MAP WORLD SIZE --------------------------------------------------------------

    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // === ACTIVE MAP INDEX ===
    // 0 = map1.txt, 1 = map2.txt (more if you extend TileManager.MAP_COUNT)
    public int currentMap = 0;

    // TILES
    public TileManager tileManager;

    // COLLISION
    public Collision collision = new Collision(this);

    // KEY INPUT
    public KeyHandler keyHandler = new KeyHandler();

    // PLAYER OBJECT
    public Player player = new Player(this, keyHandler);

    // CAMERA POSITION
    public int cameraX;
    public int cameraY;

    // GAME LOOP OBJECT
    private GameLoop gameLoop;

    // LOAD OBJECTS/OBSTACLES
    public ObjectManager objectManager;

    // CONSTRUCTOR
    public GamePanel() {
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        gameLoop = new GameLoop(this);

        // order: tiles → objects → player already exists
        tileManager = new TileManager(this);
        objectManager = new ObjectManager(this);

        // optional: starting position + starting map
        currentMap = 0; // start at map1
        // Example spawn (adjust to your map)
        player.worldX = 5 * tileSize;
        player.worldY = 5 * tileSize;
    }

    // STARTING GAME BY RUNNING GAME LOOP
    public void startGame() {
        gameLoop.start();
    }

    // ============= MAP SWITCHING API =============
    // Expose tile collision in a safe way for entities in other packages
    public boolean isTileBlocked(int col, int row) {
        return tileManager != null && tileManager.isBlocked(col, row);
    }

    // Expose OBJECT collision for Collision.java
    public boolean isObjectBlocked(int nextWorldX, int nextWorldY, Rectangle entityArea) {
        return objectManager != null && objectManager.isBlocked(nextWorldX, nextWorldY, entityArea);
    }

    /**
     * Teleport / switch map.
     *
     * @param newMapIndex index in TileManager.MAP_COUNT (0 = map1.txt, 1 = map2.txt)
     * @param playerTileCol tile column to spawn at in the new map
     * @param playerTileRow tile row to spawn at in the new map
     * @param facingDirection "up", "down", "left", "right"
     */
    public void switchToMap(int newMapIndex, int playerTileCol, int playerTileRow, String facingDirection) {
        if (newMapIndex < 0 || newMapIndex >= TileManager.MAP_COUNT) {
            System.out.println("Invalid map index: " + newMapIndex);
            return;
        }

        currentMap = newMapIndex;

        // place player
        player.worldX = playerTileCol * tileSize;
        player.worldY = playerTileRow * tileSize;
        player.direction = facingDirection;

        // recenter camera immediately on new position
        int playerWidth = tileSize * 2;
        int playerHeight = tileSize * 2;

        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;

        cameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        cameraY = player.worldY - (screenH / 2) + (playerHeight / 2);

        System.out.println("Switched to map index " + newMapIndex +
                " at tile (" + playerTileCol + "," + playerTileRow + "), facing " + facingDirection);
    }

    // UPDATES GAME LOGIC EVERY FRAME [MOVEMENT SPEED]
    public void update() {

        player.update();

        // Player size (if your player sprite is 2x2 tiles)
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
        double smoothing = 0.15; // Adjust for speed (0.1 = smoother)
        cameraX += (targetCameraX - cameraX) * smoothing;
        cameraY += (targetCameraY - cameraY) * smoothing;
    }

    // DRAWS EVERYTHING ON SCREEN EVERY FRAME
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw Tiles
        tileManager.draw(g2);

        // Draw Objects (houses, rocks, etc.)
        if (objectManager != null) {
            objectManager.draw(g2);
        }

        // Draw Player
        player.draw(g2);

        g2.dispose();
    }
}
