package src.entity;

import src.core.KeyHandler;
import src.core.GamePanel;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Entity {

    GamePanel gamePanel;
    KeyHandler keyHandler;

    // WALKING ANIMATION ARRAYS (6 frames)
    public BufferedImage[] upFrames;
    public BufferedImage[] downFrames;
    public BufferedImage[] leftFrames;
    public BufferedImage[] rightFrames;

    // IDLE ANIMATIONS
    public BufferedImage[] idleUpFrames;    // 4 frames (uses idleB1-4)
    public BufferedImage[] idleDownFrames;  // 12 frames (uses idleF1-12)
    public BufferedImage[] idleLeftFrames;  // 12 frames (uses idleL1-12)
    public BufferedImage[] idleRightFrames; // 12 frames (uses idleR1-12)

    // ANIMATION CONTROL
    int frameIndex = 0;
    int frameCounter = 0;
    int animationSpeed = 8;       // walking
    int idleAnimationSpeed = 8;   // idle (down/left/right)
    int idleUpAnimationSpeed = 24; // slower idle for up (4 frames)

    boolean moving = false;
    boolean wasMoving = false;
    String lastDirection = "down";

    public int screenX;
    public int screenY;

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;

        setDefaultValues();
        loadPlayerImages();
    }

    // INITIALIZATION OF PLAYER CHARACTER
    public void setDefaultValues() {
        // Spawn at world center
        int centerCol = gamePanel.maxWorldCol / 2;
        int centerRow = gamePanel.maxWorldRow / 2;
        worldX = centerCol * gamePanel.tileSize;
        worldY = centerRow * gamePanel.tileSize;

        // Center camera on player
        gamePanel.cameraX = worldX - (gamePanel.screenWidth / 2);
        gamePanel.cameraY = worldY - (gamePanel.screenHeight / 2);

        // Default movement and facing
        speed = 4;
        direction = "down";

        // Hitbox (1 tile wide, 1 tile tall, positioned at lower half of sprite)
        int hitboxWidth = gamePanel.tileSize;
        int hitboxHeight = gamePanel.tileSize;
        int offsetX = (gamePanel.tileSize * 2 - hitboxWidth) / 2;
        int offsetY = gamePanel.tileSize;
        solidArea.setBounds(offsetX, offsetY, hitboxWidth, hitboxHeight);
        solidAreaDefaultX = offsetX;
        solidAreaDefaultY = offsetY;
    }

    public void update() {
        int dx = 0, dy = 0;
        moving = false;
        boolean moved = false;

        // ----- INPUT -----
        if (keyHandler.upPressed) {
            dy -= 1;
            direction = "up";
            moving = true;
        }
        if (keyHandler.downPressed) {
            dy += 1;
            direction = "down";
            moving = true;
        }
        if (keyHandler.leftPressed) {
            dx -= 1;
            direction = "left";
            moving = true;
        }
        if (keyHandler.rightPressed) {
            dx += 1;
            direction = "right";
            moving = true;
        }

        // ----- MOVEMENT + COLLISION -----
        int moveX = 0;
        int moveY = 0;

        if (dx != 0 || dy != 0) {
            // Normalize diagonal speed
            if (dx != 0 && dy != 0) {
                double diagonalFactor = 1.0 / Math.sqrt(2.0);
                moveX = (int) Math.round(dx * speed * diagonalFactor);
                moveY = (int) Math.round(dy * speed * diagonalFactor);
            } else {
                moveX = dx * speed;
                moveY = dy * speed;
            }

            // Diagonal move attempt
            if (moveX != 0 && moveY != 0) {
                int nextX = worldX + moveX;
                int nextY = worldY + moveY;

                if (!gamePanel.collision.willCollide(this, nextX, nextY)) {
                    worldX = nextX;
                    worldY = nextY;
                    moved = true;
                } else {
                    // Try X only
                    boolean movedX = false;
                    boolean movedY = false;

                    if (!gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                        worldX += moveX;
                        moved = true;
                        movedX = true;
                    }

                    // Try Y only
                    if (!gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                        worldY += moveY;
                        moved = true;
                        movedY = true;
                    }

                    if (!movedX && !movedY) {
                        moving = false;
                    }
                }
            } else {
                // Axis-aligned move
                if (moveX != 0) {
                    if (!gamePanel.collision.willCollide(this, worldX + moveX, worldY)) {
                        worldX += moveX;
                        moved = true;
                    } else {
                        moving = false;
                    }
                }

                if (moveY != 0) {
                    if (!gamePanel.collision.willCollide(this, worldX, worldY + moveY)) {
                        worldY += moveY;
                        moved = true;
                    } else {
                        moving = false;
                    }
                }
            }
        }

        if (!moved) {
            moving = false;
        }

        // ----- ANIMATION -----
        updateAnimation();

        // ----- TELEPORT CHECK (AFTER MOVEMENT) -----
        handleTeleport();
    }

    // MAIN ANIMATION LOGIC (MOVING VS IDLE)
    private void updateAnimation() {
        // Reset animation when going from moving → idle or idle → moving
        if (moving != wasMoving) {
            frameIndex = 0;
            frameCounter = 0;
        }

        // Reset when direction changes
        if (!direction.equals(lastDirection)) {
            frameIndex = 0;
            frameCounter = 0;
        }

        if (moving) {
            // WALKING
            frameCounter++;
            if (frameCounter > animationSpeed) {
                frameCounter = 0;
                frameIndex = (frameIndex + 1) % 6; // 6 walking frames
            }
        } else {
            // IDLE
            frameCounter++;
            int currentIdleSpeed = direction.equals("up") ? idleUpAnimationSpeed : idleAnimationSpeed;

            if (frameCounter > currentIdleSpeed) {
                frameCounter = 0;
                switch (direction) {
                    case "up":
                        frameIndex = (frameIndex + 1) % 4;  // 4 idle up frames
                        break;
                    default:
                        frameIndex = (frameIndex + 1) % 12; // 12 idle for other dirs
                        break;
                }
            }
        }

        wasMoving = moving;
        lastDirection = direction;
    }

    // LOADING IMAGE HELPER (FROM FILE SYSTEM)
    private BufferedImage loadImageFromFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.err.println("Image not found: " + path);
                return null;
            }
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Failed to load: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // LOAD ALL PLAYER SPRITES
    public void loadPlayerImages() {

        upFrames = new BufferedImage[6];
        downFrames = new BufferedImage[6];
        leftFrames = new BufferedImage[6];
        rightFrames = new BufferedImage[6];

        idleUpFrames = new BufferedImage[12];
        idleDownFrames = new BufferedImage[12];
        idleLeftFrames = new BufferedImage[12];
        idleRightFrames = new BufferedImage[12];

        // Relative base path for assets
        String basePath =
                "src" + File.separator +
                "assets" + File.separator +
                "sprites" + File.separator +
                "player" + File.separator;

        // Walking frames
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "up" + (i + 1) + ".png");
            downFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "down" + (i + 1) + ".png");
            leftFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "left" + (i + 1) + ".png");
            rightFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "right" + (i + 1) + ".png");
        }

        // Idle UP (4 frames) → fill rest with last frame
        for (int i = 0; i < 4; i++) {
            idleUpFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleB" + (i + 1) + ".png");
        }
        BufferedImage lastUpFrame = idleUpFrames[3];
        if (lastUpFrame == null && idleUpFrames[0] != null) {
            lastUpFrame = idleUpFrames[0];
        }
        for (int i = 4; i < 12; i++) {
            idleUpFrames[i] = lastUpFrame;
        }

        // Idle DOWN (12 frames)
        for (int i = 0; i < 12; i++) {
            idleDownFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleF" + (i + 1) + ".png");
        }

        // Idle LEFT (12 frames)
        for (int i = 0; i < 12; i++) {
            idleLeftFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleL" + (i + 1) + ".png");
        }

        // Idle RIGHT (12 frames)
        for (int i = 0; i < 12; i++) {
            idleRightFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleR" + (i + 1) + ".png");
        }
    }

    // ====== TELEPORT HANDLER ======
    private void handleTeleport() {
        // find tile under the center of the player's hitbox
        int playerCol = (worldX + solidArea.x + solidArea.width / 2) / gamePanel.tileSize;
        int playerRow = (worldY + solidArea.y + solidArea.height / 2) / gamePanel.tileSize;

        int tileNum = gamePanel.tileManager.getTileNum(playerCol, playerRow);

        // EXAMPLE:
        // MAP1 TO MAP2
        if (gamePanel.currentMap == 0 && tileNum == 5) { // 5 = teleport tile ID
            gamePanel.switchToMap(
                    1,      // newMapIndex -> map2.txt
                    16,     // playerTileCol
                    2,     // playerTileRow
                    "down"  // facing
            );
            return;
            
        }

        // MAP2 TO MAP 1
        if (gamePanel.currentMap == 1 && tileNum == 5) {
            gamePanel.switchToMap(
                    0,
                    16,
                    18,
                    "up"
            );
        }
    }

    // RENDER SPRITE
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (moving) {
            switch (direction) {
                case "up":    image = upFrames[frameIndex]; break;
                case "down":  image = downFrames[frameIndex]; break;
                case "left":  image = leftFrames[frameIndex]; break;
                case "right": image = rightFrames[frameIndex]; break;
            }
        } else {
            switch (direction) {
                case "up":    image = idleUpFrames[frameIndex]; break;
                case "down":  image = idleDownFrames[frameIndex]; break;
                case "left":  image = idleLeftFrames[frameIndex]; break;
                case "right": image = idleRightFrames[frameIndex]; break;
            }
        }

        if (image != null) {
            int spriteWidth = gamePanel.tileSize * 4;
            int spriteHeight = gamePanel.tileSize * 4;

            // Get actual screen size from the panel
            int screenW = gamePanel.getWidth();
            int screenH = gamePanel.getHeight();

            // Use default size if panel not sized yet
            if (screenW <= 0) screenW = gamePanel.screenWidth;
            if (screenH <= 0) screenH = gamePanel.screenHeight;

            // Draw player in the center of the screen
            int drawX = (screenW / 2) - (spriteWidth / 2);
            int drawY = (screenH / 2) - (spriteHeight / 2);

            g2.drawImage(image, drawX, drawY, spriteWidth, spriteHeight, null);
        }
    }
}
