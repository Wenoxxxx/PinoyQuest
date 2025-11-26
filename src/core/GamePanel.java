package src.core;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import src.entity.Player;
import src.tile.ObjectManager;
import src.tile.TileManager;

public class GamePanel extends JPanel {

    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale; // 48x48 pixels
    public final int maxScreenCol = 40;
    public final int maxScreenRow = 22;

    // Default screen size (used as fallback)
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // MAP WORLD SIZE --------------------------------------------------------------
    public final int maxWorldCol = 31; // WIDTH | number of tiles horizontally
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
    public Collision collision;

    // KEY INPUT
    public KeyHandler keyHandler;

    // PLAYER OBJECT
    public Player player;

    // CAMERA POSITION
    public int cameraX;
    public int cameraY;
    private boolean cameraInitialized = false;

    // GAME LOOP OBJECT
    private GameLoop gameLoop;

    // LOAD OBJECTS/OBSTACLES
    public ObjectManager objectManager;

    // ===================== GAME STATES =====================
    public static final int STATE_MENU     = 0;
    public static final int STATE_PLAY     = 1;
    public static final int STATE_SETTINGS = 2;

    public int gameState = STATE_MENU;

    // ===================== MENU UI =====================
    private BufferedImage menuBackground;
    private BufferedImage buttonSprite;

    // 0 = Start, 1 = Resume, 2 = Settings, 3 = Quit
    public int menuSelectedIndex = 0;
    public final String[] menuOptions = { "Start", "Resume", "Settings", "Quit" };
    public boolean canResume = false; // becomes true after first start

    // ===================== CAMERA HELPERS =====================
    private void centerCameraOnPlayer(int screenW, int screenH) {
        int playerWidth = tileSize * 2;   // 2x2 tiles
        int playerHeight = tileSize * 2;

        cameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        cameraY = player.worldY - (screenH / 2) + (playerHeight / 2);
    }

    // ===================== CONSTRUCTOR =====================
    public GamePanel() {
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        // Create helpers
        collision = new Collision(this);
        keyHandler = new KeyHandler(this);
        this.addKeyListener(keyHandler);

        gameLoop = new GameLoop(this);

        // order: tiles → objects → player
        tileManager = new TileManager(this);
        objectManager = new ObjectManager(this);

        // create player
        player = new Player(this, keyHandler);

        // starting map & position (for when the game actually starts)
        currentMap = 0; // map1
        player.worldX = 15 * tileSize; // middle column (0..30 -> 15)
        player.worldY = 10 * tileSize; // middle row (0..20 -> 10)

        centerCameraOnPlayer(screenWidth, screenHeight);
        cameraInitialized = false;

        loadMenuSprites();
    }

    private void loadMenuSprites() {
        try {
            // TODO: adjust these paths to your actual files
            menuBackground = ImageIO.read(new File("src/assets/ui/mainMenu/BG.png"));
            buttonSprite   = ImageIO.read(new File("src/assets/ui/button.png"));
        } catch (IOException e) {
            System.out.println("Failed to load menu sprites:");
            e.printStackTrace();
        }
    }

    // STARTING GAME BY RUNNING GAME LOOP (called from your main)
    public void startGame() {
        gameLoop.start();
    }

    // ===================== MAP SWITCHING API =====================

    // Expose tile collision in a safe way for entities in other packages
    public boolean isTileBlocked(int col, int row) {
        return tileManager != null && tileManager.isBlocked(col, row);
    }

    // Expose OBJECT collision for Collision.java
    public boolean isObjectBlocked(int nextWorldX, int nextWorldY, Rectangle entityArea) {
        return objectManager != null && objectManager.isBlocked(nextWorldX, nextWorldY, entityArea);
    }

    /**
     * @param newMapIndex     // index in TileManager.MAP_COUNT (0 = map1.txt, 1 = map2.txt)
     * @param playerTileCol   // tile column to spawn at in the new map
     * @param playerTileRow   // tile row to spawn at in the new map
     * @param facingDirection // "up", "down", "left", "right"
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

        cameraInitialized = true;

        System.out.println("Switched to map index " + newMapIndex +
                " at tile (" + playerTileCol + "," + playerTileRow + "), facing " + facingDirection);
    }

    // ===================== GAME STATE HELPERS =====================

    // Called when selecting "Start" from menu
    public void startNewGame() {
        // reset player if you want a fresh run
        currentMap = 0;
        player.worldX = 15 * tileSize;
        player.worldY = 10 * tileSize;
        player.direction = "down"; // or whatever default

        // re-center camera and force snap next frame
        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;
        centerCameraOnPlayer(screenW, screenH);
        cameraInitialized = false;

        canResume = true;
        gameState = STATE_PLAY;
    }

    // Called when selecting "Resume"
    public void resumeGame() {
        if (!canResume) return;
        gameState = STATE_PLAY;
        cameraInitialized = false; // snap camera again
    }

    // Called when selecting "Settings"
    public void openSettings() {
        gameState = STATE_SETTINGS;
    }

    // ===================== UPDATE =====================

    // UPDATES GAME LOGIC EVERY FRAME [MOVEMENT SPEED]
    public void update() {

        if (gameState == STATE_MENU) {
            // Add menu animations here if you want (e.g., blinking text)
            return;
        }

        if (gameState == STATE_SETTINGS) {
            // Settings logic (sliders, etc.) can go here later
            return;
        }

        // ===== STATE_PLAY =====
        player.update();
        objectManager.update();

        // Player size (if your player sprite is 2x2 tiles)
        int playerWidth = tileSize * 2;
        int playerHeight = tileSize * 2;

        // Get actual screen size from the panel
        int screenW = getWidth();
        int screenH = getHeight();

        // Use default size if panel not sized yet
        if (screenW <= 0) screenW = screenWidth;
        if (screenH <= 0) screenH = screenHeight;

        // Center camera on player
        int targetCameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        int targetCameraY = player.worldY - (screenH / 2) + (playerHeight / 2);

        if (!cameraInitialized) {
            // First frame after spawn or resume: SNAP to target (no hover)
            cameraX = targetCameraX;
            cameraY = targetCameraY;
            cameraInitialized = true;
        } else {
            // Next frames: smooth follow (LERP)
            double smoothing = 0.15; // Adjust for speed
            cameraX += (targetCameraX - cameraX) * smoothing;
            cameraY += (targetCameraY - cameraY) * smoothing;
        }
    }

    // ===================== DRAWING =====================

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == STATE_MENU) {
            drawMainMenu(g2);
        } else if (gameState == STATE_SETTINGS) {
            drawSettingsScreen(g2);
        } else if (gameState == STATE_PLAY) {
            drawGame(g2);
        }

        g2.dispose();
    }

    private void drawGame(Graphics2D g2) {
        // Draw Tiles
        tileManager.draw(g2);

        // ========= PLAYER FEET TILE ROW (for overlap layering) =========
        int playerFeetY = player.worldY + player.solidArea.y + player.solidArea.height;
        int playerFeetRow = playerFeetY / tileSize;

        // Draw objects that should appear BEHIND the player
        if (objectManager != null) {
            objectManager.drawBehindPlayer(g2, playerFeetRow);
        }

        // Draw Player (always centered on screen)
        player.draw(g2);

        // Draw objects that should appear IN FRONT of the player
        if (objectManager != null) {
            objectManager.drawInFrontOfPlayer(g2, playerFeetRow);
        }

        // HUD on top of everything
        player.drawHud(g2);
    }

    private void drawMainMenu(Graphics2D g2) {
        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;

        // ===================== BACKGROUND (NO STRETCH, KEEP ASPECT RATIO) =====================
        if (menuBackground != null) {
            int bgW = menuBackground.getWidth();
            int bgH = menuBackground.getHeight();

            // Scale needed to fit the screen
            double scaleX = (double) screenW / bgW;
            double scaleY = (double) screenH / bgH;

            // Use the smaller scale so the image is never stretched
            double scale = Math.min(scaleX, scaleY);

            int drawW = (int) (bgW * scale);
            int drawH = (int) (bgH * scale);

            int x = (screenW - drawW) / 2;
            int y = (screenH - drawH) / 2;

            g2.drawImage(menuBackground, x, y, drawW, drawH, null);
        } else {
            // fallback if image fails to load
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenW, screenH);
        }

        // ===================== TITLE =====================
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.WHITE);


        // ===================== BUTTONS =====================
        int buttonWidth  = (buttonSprite != null) ? buttonSprite.getWidth()  : 260;
        int buttonHeight = (buttonSprite != null) ? buttonSprite.getHeight() : 60;

        int centerX = screenW / 2;
        int startY  = screenH / 2 - 2 * (buttonHeight + 10)+60;
        int gap     = 20;

        g2.setFont(new Font("Arial", Font.PLAIN, 26));

        for (int i = 0; i < menuOptions.length; i++) {
            int x = centerX - (buttonWidth / 2);
            int y = startY + i * (buttonHeight + gap);

            // button sprite
            if (buttonSprite != null) {
                // drawn at its natural resolution, not stretched
                g2.drawImage(buttonSprite, x, y, buttonWidth, buttonHeight, null);
            } else {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }

            // highlight
            if (i == menuSelectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }

            // text
            String label = menuOptions[i];
            if (label.equals("Resume") && !canResume) {
                g2.setColor(Color.GRAY);
            } else {
                g2.setColor(Color.WHITE);
            }

            int textWidth = g2.getFontMetrics().stringWidth(label);
            int textX = centerX - textWidth / 2;
            int textY = y + (buttonHeight / 2) + g2.getFontMetrics().getAscent() / 2 - 4;

            g2.drawString(label, textX, textY);
        }

        // ===================== HINT TEXT =====================
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        String hint = "Use W/S or ↑/↓ to move, Enter to select";
        int hintW = g2.getFontMetrics().stringWidth(hint);
        g2.setColor(Color.WHITE);
        g2.drawString(hint, (screenW - hintW) / 2, screenH - 40);
    }


    private void drawSettingsScreen(Graphics2D g2) {
        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;

        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(0, 0, screenW, screenH);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        String s = "Settings (placeholder)";
        int w = g2.getFontMetrics().stringWidth(s);
        g2.drawString(s, (screenW - w) / 2, screenH / 2);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String back = "Press ESC to go back";
        int w2 = g2.getFontMetrics().stringWidth(back);
        g2.drawString(back, (screenW - w2) / 2, screenH / 2 + 40);
    }
}
