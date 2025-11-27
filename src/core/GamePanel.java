package src.core;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import src.entity.Player;
import src.entity.mobs.WhiteLady;
import src.tile.ObjectManager;
import src.tile.TileManager;

// MOBS
import java.util.ArrayList;
import java.util.List;
import src.entity.mobs.WhiteLady;
import src.entity.mobs.MobManager;


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
    // 0 = Start, 1 = Resume, 2 = Settings, 3 = Quit
    private BufferedImage[] buttonSprites = new BufferedImage[4];

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

    // ===================== MOBS ==============================
    public List<WhiteLady> whiteLadies = new ArrayList<>();
    public MobManager mobManager;
    



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

        mobManager = new MobManager(this);
        whiteLadies.clear();
    }

    private void loadMenuSprites() {
        try {
            // background
            menuBackground = ImageIO.read(new File("src/assets/ui/mainMenu/BG.png"));

            // buttons: make sure these paths match your actual files
            buttonSprites[0] = ImageIO.read(new File("src/assets/ui/mainMenu/btn1.png")); // Start
            buttonSprites[1] = ImageIO.read(new File("src/assets/ui/mainMenu/btn2.png")); // Resume
            buttonSprites[2] = ImageIO.read(new File("src/assets/ui/mainMenu/btn3.png")); // Settings
            buttonSprites[3] = ImageIO.read(new File("src/assets/ui/mainMenu/btn4.png")); // Quit

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

    if (mobManager != null) {
        if (currentMap == 1) {  
            mobManager.spawnMobsForMap(1);  // load mobs_map2.txt
        } else {
            whiteLadies.clear();            // clear all mobs in other maps
        }
    }
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

        // TESTT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (mobManager != null) {
            mobManager.update();
        }
        
        
        for (WhiteLady wl : whiteLadies) {
            wl.update();
        }

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

        int playerFeetY = player.worldY + player.solidArea.y + player.solidArea.height;
        int playerFeetRow = playerFeetY / tileSize;

        // Objects behind player
        if (objectManager != null) {
            objectManager.drawBehindPlayer(g2, playerFeetRow);
        }

        // Player
        player.draw(g2);

        // Objects in front of player
        if (objectManager != null) {
            objectManager.drawInFrontOfPlayer(g2, playerFeetRow);
        }

        // MOBS AFTER OBJECTS, SO THEY AREN'T HIDDEN
        if (mobManager != null) {
            mobManager.draw(g2);
        }

        // HUD
        player.drawHud(g2);
    }



    private void drawMainMenu(Graphics2D g2) {
        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;

        // ===================== BACKGROUND (NO STRETCH, KEEP ASPECT RATIO) =====================
        if (menuBackground != null) {
            int bgW = menuBackground.getWidth();
            int bgH = menuBackground.getHeight();

            double scaleX = (double) screenW / bgW;
            double scaleY = (double) screenH / bgH;
            double scale = Math.min(scaleX, scaleY);

            int drawW = (int) (bgW * scale);
            int drawH = (int) (bgH * scale);

            int x = (screenW - drawW) / 2;
            int y = (screenH - drawH) / 2;

            g2.drawImage(menuBackground, x, y, drawW, drawH, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenW, screenH);
        }

        // ===================== TITLE (optional) =====================
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.WHITE);
        // you can draw a title string here if you want

        // ===================== BUTTONS =====================

        int defaultButtonWidth  = 260;
        int defaultButtonHeight = 60;

        // Scale factor (try 1.5, 1.8, 2.0, etc.)
        double buttonScale = 5.5;

        // use first button’s height for spacing, if available
        int baseButtonHeight = defaultButtonHeight;
        if (buttonSprites[0] != null) {
            baseButtonHeight = (int)(buttonSprites[0].getHeight() * buttonScale);
        }

        int centerX = screenW / 2;
        int startY  = screenH / 2 - 2 * (baseButtonHeight + 10) + 60;
        int gap     = 5;

        g2.setFont(new Font("Arial", Font.PLAIN, 26));

        for (int i = 0; i < menuOptions.length; i++) {
            BufferedImage btnImg = buttonSprites[i];

            int rawWidth  = (btnImg != null) ? btnImg.getWidth()  : defaultButtonWidth;
            int rawHeight = (btnImg != null) ? btnImg.getHeight() : defaultButtonHeight;

            // scaling here
            int buttonWidth  = (int)(rawWidth  * buttonScale);
            int buttonHeight = (int)(rawHeight * buttonScale);

            int x = centerX - (buttonWidth / 2);
            int y = startY + i * (baseButtonHeight + gap);

            // button sprite or fallback
            if (btnImg != null) {
                g2.drawImage(btnImg, x, y, buttonWidth, buttonHeight, null);
            } else {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }

            // highlight border if selected
            if (i == menuSelectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }
            
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
