package src.core;

import javax.swing.JPanel;
import java.awt.*;

import src.entity.Player;
import src.entity.mobs.WhiteLady;
import src.items.ItemManager;
import src.tile.ObjectManager;
import src.tile.TileManager;
import src.ui.UI;
import src.ui.ActionBarUI;
import src.ui.MainMenuUI;

// MOBS
import java.util.ArrayList;
import java.util.List;
import src.entity.mobs.SawTrap;
import src.entity.mobs.MobManager;

public class GamePanel extends JPanel {

    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale;
    public final int maxScreenCol = 40;
    public final int maxScreenRow = 22;

    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // MAP WORLD SIZE
    public final int maxWorldCol = 31;
    public final int maxWorldRow = 21;

    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    public int currentMap = 0;

    // SYSTEMS
    public TileManager tileManager;
    public Collision collision;
    public KeyHandler keyHandler;
    public Player player;

    // CAMERA
    public int cameraX;
    public int cameraY;
    private boolean cameraInitialized = false;

    private GameLoop gameLoop;
    public ObjectManager objectManager;

    // GAME STATES
    public static final int STATE_MENU = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_SETTINGS = 2;
    public static final int STATE_INVENTORY = 3;

    public int gameState = STATE_MENU;

    // MENU SELECTION (still needed)
    public int menuSelectedIndex = 0;
    public final String[] menuOptions = { "Start", "Resume", "Settings", "Quit" };
    public boolean canResume = false;

    // MOBS
    public List<WhiteLady> whiteLadies = new ArrayList<>();
    public List<SawTrap> sawTraps = new ArrayList<>();
    public MobManager mobManager;

    // UI
    public UI ui;
    public MainMenuUI mainMenuUI;
    public boolean showInventory = false;

    // ITEM
    public ItemManager itemManager;
    public ActionBarUI actionBarUI;

    // ===================== CONSTRUCTOR =====================
    public GamePanel() {

<<<<<<< Updated upstream
=======
        // PANEL SETTINGS
>>>>>>> Stashed changes
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);

<<<<<<< Updated upstream
        collision = new Collision(this);
        keyHandler = new KeyHandler(this);
        this.addKeyListener(keyHandler);

=======
        // KEY HANDLER
        keyHandler = new KeyHandler(this);
        this.addKeyListener(keyHandler);

        // GAME LOOP
>>>>>>> Stashed changes
        gameLoop = new GameLoop(this);

        // SYSTEMS
        tileManager = new TileManager(this);
        objectManager = new ObjectManager(this);
        itemManager = new ItemManager(this);
        collision = new Collision(this);

        // PLAYER
        player = new Player(this, keyHandler);
        player.worldX = 15 * tileSize;
        player.worldY = 10 * tileSize;

        centerCameraOnPlayer(screenWidth, screenHeight);

        // MOBS
        mobManager = new MobManager(this);
        whiteLadies.clear();

        // NEW UI SYSTEMS
        ui = new UI(this, player);
        mainMenuUI = new MainMenuUI(this);
        actionBarUI = new ActionBarUI(this, player);
<<<<<<< Updated upstream
=======

        // AI WAVE SPAWNER
        map3Spawner = new Map3EnemySpawner(this);

>>>>>>> Stashed changes
    }

    private void centerCameraOnPlayer(int screenW, int screenH) {
        int playerWidth = tileSize * 2;
        int playerHeight = tileSize * 2;

        cameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        cameraY = player.worldY - (screenH / 2) + (playerHeight / 2);
    }

    // ===================== MAP SWITCH =====================

    public boolean isTileBlocked(int col, int row) {
        return tileManager != null && tileManager.isBlocked(col, row);
    }

    public boolean isObjectBlocked(int nextWorldX, int nextWorldY, Rectangle entityArea) {
        return objectManager != null && objectManager.isBlocked(nextWorldX, nextWorldY, entityArea);
    }

    public void switchToMap(int newMapIndex, int playerTileCol, int playerTileRow, String facingDirection) {

        if (newMapIndex < 0 || newMapIndex >= TileManager.MAP_COUNT) {
            System.out.println("[ERROR] Invalid map index: " + newMapIndex);
            return;
        }

        currentMap = newMapIndex;

        if (tileManager != null)
            tileManager.loadMap(newMapIndex);

        if (mobManager != null)
            mobManager.spawnMobsForMap(newMapIndex);

        player.worldX = playerTileCol * tileSize;
        player.worldY = playerTileRow * tileSize;
        player.direction = facingDirection;

        int screenW = (getWidth() > 0 ? getWidth() : screenWidth);
        int screenH = (getHeight() > 0 ? getHeight() : screenHeight);

        int playerWidth = tileSize * 2;
        int playerHeight = tileSize * 2;

        cameraX = player.worldX - (screenW / 2) + (playerWidth / 2);
        cameraY = player.worldY - (screenH / 2) + (playerHeight / 2);

        cameraInitialized = true;

        System.out.println("[GamePanel] Switched to map " + newMapIndex);
    }

    // ===================== GAME STATE ACTIONS =====================
    public void startNewGame() {

        currentMap = 0;
        player.worldX = 15 * tileSize;
        player.worldY = 10 * tileSize;
        player.direction = "down";

        // initial mob spawn (moved here)
        mobManager.spawnMobsForMap(currentMap);

        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;
        centerCameraOnPlayer(screenW, screenH);

        cameraInitialized = false;

        canResume = true;
        gameState = STATE_PLAY;
    }


    public void resumeGame() {
        if (!canResume)
            return;
        gameState = STATE_PLAY;
        cameraInitialized = false;
    }

    public void openSettings() {
        gameState = STATE_SETTINGS;
    }
    

    // ===================== UPDATE =====================
    public void update() {
<<<<<<< Updated upstream

        if (gameState == STATE_MENU) return;
        if (gameState == STATE_SETTINGS) return;

        // GAMEPLAY
        player.update();
        objectManager.update();
        itemManager.update();

        if (mobManager != null)
            mobManager.update();

        for (WhiteLady wl : whiteLadies)
            wl.update();
=======
    try {
        // Always update the UI for the current game state
        if (gameState == STATE_GAME_OVER) {
            System.out.println("[GamePanel] In GAME_OVER state, calling gameOverUI.update()");
            try {
                gameOverUI.update();
            } catch (Exception e) {
                System.err.println("[ERROR] Error in gameOverUI.update(): " + e.getMessage());
                e.printStackTrace();
                // Try to recover by going back to menu
                gameState = STATE_MENU;
                return;
            }
        }
        
        // Don't update game logic if in menu or game over
        if (gameState == STATE_MENU || gameState == STATE_GAME_OVER) {
            // Still update projectiles to allow any active animations to continue
            try {
                updateProjectiles();
            } catch (Exception e) {
                System.err.println("[ERROR] Error in updateProjectiles(): " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        // GAMEPLAY - Only update these when actually playing
        try {
            player.update();
            objectManager.update();
            itemManager.update();

            if (mobManager != null) {
                mobManager.update();
            }

            for (WhiteLady wl : whiteLadies) {
                wl.update();
            }

            updateProjectiles();
        } catch (Exception e) {
            System.err.println("[ERROR] Error in game update loop: " + e.getMessage());
            e.printStackTrace();
            // Try to recover by going to game over state
            gameState = STATE_GAME_OVER;
        }

        // -------------------------------
        // UPDATE WAVES IF IN MAP 3 (index 2)
        // -------------------------------
        if (currentMap == 2) {
            map3Spawner.update(player);
        }
>>>>>>> Stashed changes

    }

    // CAMERA LOGIC
    public void updateCamera() {

        // Player center (world coordinates)
        int playerCenterX = player.worldX + tileSize / 2;
        int playerCenterY = player.worldY + tileSize / 2;

        // Get screen size or fallback
        int screenW = getWidth() > 0 ? getWidth() : screenWidth;
        int screenH = getHeight() > 0 ? getHeight() : screenHeight;

        // Compute target camera position
        int targetCameraX = playerCenterX - (screenW / 2);
        int targetCameraY = playerCenterY - (screenH / 2);

        // Initial snap
        if (!cameraInitialized) {
            cameraX = targetCameraX;
            cameraY = targetCameraY;
            cameraInitialized = true;
        } else {
            // Smooth follow
            double smoothing = 0.08; // smoother, less floaty
            cameraX += (targetCameraX - cameraX) * smoothing;
            cameraY += (targetCameraY - cameraY) * smoothing;
        }

        // --- Clamp camera inside world tile boundaries ---
        int worldWidthPixels = maxWorldCol * tileSize;
        int worldHeightPixels = maxWorldRow * tileSize;

        cameraX = Math.max(0, Math.min(cameraX, worldWidthPixels - screenW));
        cameraY = Math.max(0, Math.min(cameraY, worldHeightPixels - screenH));
    }


    // ===================== START GAME=====================

    public void startGame() {
        gameLoop.start();
    }

    // ===================== DRAW =====================
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

<<<<<<< Updated upstream
        if (gameState == STATE_MENU) {
            mainMenuUI.draw(g2);
        } else if (gameState == STATE_SETTINGS) {
            drawSettingsScreen(g2);
        } else if (gameState == STATE_PLAY) {
            drawGame(g2);
        } else if (gameState == STATE_INVENTORY) {
            // Draw the game FROZEN in the background
            drawGame(g2);

            // Then draw the inventory popup on top
            ui.getInventoryUI().draw(g2);
=======
        try {
            switch (gameState) {
                case STATE_MENU:
                    mainMenuUI.draw(g2);
                    break;
                case STATE_SETTINGS:
                    // Settings screen removed, fall back to menu
                    gameState = STATE_MENU;
                    mainMenuUI.draw(g2);
                    break;
                case STATE_PLAY:
                case STATE_INVENTORY:
                    // Always draw the game world first
                    drawGame(g2);

                    // Draw inventory on top if active
                    if (gameState == STATE_INVENTORY) {
                        ui.getInventoryUI().draw(g2);
                    }
                    break;
                case STATE_GAME_OVER:
                    // Draw the last frame of the game before showing game over
                    drawGame(g2);
                    // Then draw the game over UI on top
                    gameOverUI.draw(g2);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error in paintComponent: " + e.getMessage());
            e.printStackTrace();
        } finally {
            g2.dispose();
>>>>>>> Stashed changes
        }

        g2.dispose();
    }

    private void drawGame(Graphics2D g2) {

        tileManager.draw(g2);

        int playerFeetY = player.worldY + player.solidArea.y + player.solidArea.height;
        int playerFeetRow = playerFeetY / tileSize;

        // ITEMS BEHIND PLAYER
        if (itemManager != null)
            itemManager.drawBehindPlayer(g2, playerFeetRow);

        // OBJECTS BEHIND PLAYER
        if (objectManager != null)
            objectManager.drawBehindPlayer(g2, playerFeetRow);

        // DRAW PLAYER
        player.draw(g2);

        // OBJECTS IN FRONT
        if (objectManager != null)
            objectManager.drawInFrontOfPlayer(g2, playerFeetRow);

        // ITEMS IN FRONT
        if (itemManager != null)
            itemManager.drawInFrontOfPlayer(g2, playerFeetRow);

        // MOBS
        if (mobManager != null)
            mobManager.draw(g2);

        // UI
        ui.draw(g2);

        // Hotbar under HUD (only while playing)
        if (gameState == STATE_PLAY) {
            actionBarUI.draw(g2);
        }
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
