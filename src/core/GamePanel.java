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

    public final int screenWidth = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px

    public final int maxWorldCol = 100; // example: number of tiles horizontally
    public final int maxWorldRow = 100; // example: number of tiles vertically
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // // [TEST] PLAYER POSITION
    // int playerX = 100;
    // int playerY = 100;
    // int playerSpeed = 4;

    // for tiles
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

        // BASIC SETUP
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));// setting the size of game window
        this.setBackground(Color.BLACK);// Background color of the panel
        this.setDoubleBuffered(true); // Enable double buffering to prevent flickering
        this.addKeyListener(keyHandler); // Add the keyboard listener (so it detects key presses)
        this.setFocusable(true); // Makes sure the panel can receive keyboard input

        // INITIALIZING GAME LOOP
        gameLoop = new GameLoop(this);

    }

    // STARTING GAME BY RUNNING GAME LOOP
    public void startGame() {
        gameLoop.start();
    }

    // UPDATES GAME LOGIC EVERY FRAME [MOVEMENT SPEED]
    public void update() {
        // if (keyHandler.upPressed) playerY -= playerSpeed;
        // if (keyHandler.downPressed) playerY += playerSpeed;
        // if (keyHandler.leftPressed) playerX -= playerSpeed;
        // if (keyHandler.rightPressed) playerX += playerSpeed;

        // int dx = 0;
        // int dy = 0;

        // if (keyHandler.upPressed) dy -= 1;
        // if (keyHandler.downPressed) dy += 1;
        // if (keyHandler.leftPressed) dx -= 1;
        // if (keyHandler.rightPressed) dx += 1;

        // // If moving diagonally, normalize speed
        // if (dx != 0 && dy != 0) {
        // playerX += dx * (playerSpeed / Math.sqrt(2));
        // playerY += dy * (playerSpeed / Math.sqrt(2));
        // } else {
        // playerX += dx * playerSpeed;
        // playerY += dy * playerSpeed;
        // }

        player.update();

        // Player size in pixels
        int playerWidth = tileSize * 4;
        int playerHeight = tileSize * 4;

        // Target camera position centered on player
        int targetCameraX = player.worldX - (screenWidth / 2) + (playerWidth / 2);
        int targetCameraY = player.worldY - (screenHeight / 2) + (playerHeight / 2);

        // Smooth camera movement (LERP)
        double smoothing = 0.1; // 0.1 = 10% per frame; smaller = smoother
        cameraX += (targetCameraX - cameraX) * smoothing;
        cameraY += (targetCameraY - cameraY) * smoothing;

        /*
         * // Clamp camera to world boundaries
         * if (cameraX < 0)
         * cameraX = 0;
         * if (cameraY < 0)
         * cameraY = 0;
         * if (cameraX > worldWidth - screenWidth)
         * cameraX = worldWidth - screenWidth;
         * if (cameraY > worldHeight - screenHeight)
         * cameraY = worldHeight - screenHeight;
         */
    }

    // DRAWS EVERYTHING OM SCREEN EVERY FRAME
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw Tiles

        tileManager.draw(g2);

        // // TEMPORARY CHARACTER
        // g2.setColor(Color.WHITE);
        // g2.fillRect(playerX, playerY, tileSize, tileSize);

        player.draw(g2);

        g2.dispose();

    }

}
