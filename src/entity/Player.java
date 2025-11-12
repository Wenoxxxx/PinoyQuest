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
    public BufferedImage[] idleUpFrames; // 4 frames (uses idleB1-4)
    public BufferedImage[] idleDownFrames; // 12 frames (uses idleF1-12)
    public BufferedImage[] idleLeftFrames; // 12 frames (uses idleL1-12)
    public BufferedImage[] idleRightFrames; // 12 frames (uses idleR1-12)

    // ANIMATION CONTROL
    int frameIndex = 0;
    int frameCounter = 0;
    int animationSpeed = 8; // 30 frames ≈ 0.5 sec at 60 FPS for walking
    int idleAnimationSpeed = 8; // 8 frames for idle animation (for 12-frame directions)
    int idleUpAnimationSpeed = 24; // 24 frames for up idle (slower to match 12-frame timing: 4 frames * 24 = 96,
                                   // same as 12 frames * 8 = 96)

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
        worldX = 770;
        worldY = 400;
        speed = 3;
        direction = "down"; // default facing
    }

    public void update() {
        int dx = 0, dy = 0;
        moving = false;

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

        // Normalize diagonal movement to match cardinal movement speed
        if (dx != 0 && dy != 0) {
            // Diagonal movement: normalize by 1/sqrt(2) ≈ 0.707
            double diagonalFactor = 1.0 / Math.sqrt(2.0);
            worldX += (int) (dx * speed * diagonalFactor);
            worldY += (int) (dy * speed * diagonalFactor);
        } else {
            // Cardinal movement: normal speed
            worldX += dx * speed;
            worldY += dy * speed;
        }

        updateAnimation();
    }

    // MAIN ANIMATION LOGIC (MOVING VS IDLE)
    private void updateAnimation() {
        // Fast transition: reset animation when switching between idle and movement
        if (moving != wasMoving) {
            // State changed - instantly reset to first frame for smooth transition
            frameIndex = 0;
            frameCounter = 0;
        }

        // Reset animation when direction changes while in the same state
        if (!direction.equals(lastDirection)) {
            frameIndex = 0;
            frameCounter = 0;
        }

        // Animate based on current state
        if (moving) {
            // WALKING: animate walking frames
            frameCounter++;
            if (frameCounter > animationSpeed) {
                frameCounter = 0;
                frameIndex = (frameIndex + 1) % 6;
            }
        } else {
            // IDLE: animate idle frames
            frameCounter++;
            // Use different animation speed for up direction to match timing with other
            // directions
            int currentIdleSpeed = (direction.equals("up")) ? idleUpAnimationSpeed : idleAnimationSpeed;

            if (frameCounter > currentIdleSpeed) {
                frameCounter = 0;
                // Idle animation cycle based on direction
                switch (direction) {
                    case "up":
                        frameIndex = (frameIndex + 1) % 4; // UP has only 4 frames, but slower speed matches timing
                        break;
                    default:
                        frameIndex = (frameIndex + 1) % 12; // Other directions have 12 frames
                        break;
                }
            }
        }

        // Update state tracking for next frame
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

        // Relative base path for assets (no System.getProperty)
        String basePath = 
                "src" + File.separator + 
                "assets" + File.separator + 
                "sprites" + File.separator + 
                "player" + File.separator;

        // Load walking frames
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "up" + (i + 1) + ".png");
            downFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "down" + (i + 1) + ".png");
            leftFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "left" + (i + 1) + ".png");
            rightFrames[i] = loadImageFromFile(basePath + "movement" + File.separator + "right" + (i + 1) + ".png");
        }

        // Load idle animations
        // UP (back) - 4 frames using idleB (back), duplicate last frame for remaining
        // slots
        for (int i = 0; i < 4; i++) {
            idleUpFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleB" + (i + 1) + ".png");
        }
        // Fill remaining slots with last frame (animation only uses first 4, but array
        // needs 12)
        BufferedImage lastUpFrame = idleUpFrames[3];
        if (lastUpFrame == null && idleUpFrames[0] != null) {
            lastUpFrame = idleUpFrames[0]; // Fallback to first frame if last is null
        }
        for (int i = 4; i < 12; i++) {
            idleUpFrames[i] = lastUpFrame;
        }

        // DOWN (forward) - 12 frames using idleF (forward)
        for (int i = 0; i < 12; i++) {
            idleDownFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleF" + (i + 1) + ".png");
        }

        // LEFT - 12 frames using idleL
        for (int i = 0; i < 12; i++) {
            idleLeftFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleL" + (i + 1) + ".png");
        }

        // RIGHT - 12 frames using idleR
        for (int i = 0; i < 12; i++) {
            idleRightFrames[i] = loadImageFromFile(basePath + "idle" + File.separator + "idleR" + (i + 1) + ".png");
        }
    }

    // RENDER SPRITE
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

    if (moving) {
        switch (direction) {
            case "up": image = upFrames[frameIndex]; break;
            case "down": image = downFrames[frameIndex]; break;
            case "left": image = leftFrames[frameIndex]; break;
            case "right": image = rightFrames[frameIndex]; break;
        }
    } else {
        switch (direction) {
            case "up": image = idleUpFrames[frameIndex]; break;
            case "down": image = idleDownFrames[frameIndex]; break;
            case "left": image = idleLeftFrames[frameIndex]; break;
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
        if (screenW <= 0) {
            screenW = gamePanel.screenWidth;
        }
        if (screenH <= 0) {
            screenH = gamePanel.screenHeight;
        }

        // Draw player in the center of the screen
        int drawX = (screenW / 2) - (spriteWidth / 2);
        int drawY = (screenH / 2) - (spriteHeight / 2);

        g2.drawImage(image, drawX, drawY, spriteWidth, spriteHeight, null);
    }
    }

}
