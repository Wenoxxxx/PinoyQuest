package src.entity.mobs;

import src.entity.Entity;
import src.core.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SawTrap extends Entity {

    private static final Random RNG = new Random();

    private final GamePanel gamePanel;

    private BufferedImage[] frames;
    private boolean spritesLoaded = false;

    private int frameIndex = 0;
    private int frameCounter = 0;
    private final int frameSpeed = 8;

    // Vertical patrol
    private final int patrolTiles = 3;
    private final int patrolDistancePx;
    private final int startY;
    private boolean movingDown = true;

    private final int renderWidth;
    private final int renderHeight;

    // ==== NEW: per-instance random behavior ====
    // speed is inherited from Entity; we change it per trap
    private int startDelay;          // frames to wait before starting to move
    private int startDelayCounter = 0;

    private int pauseFramesAtTop;    // how long to pause at top
    private int pauseFramesAtBottom; // how long to pause at bottom
    private int pauseCounter = 0;
    private boolean isPaused = false;
    private int currentPauseTarget = 0;
    // ===========================================

    public SawTrap(GamePanel gp, int worldX, int worldY) {
        this.gamePanel = gp;

        this.worldX = worldX;
        this.worldY = worldY;

        // ==== NEW: randomize speed and direction ====
        this.speed = 1 + RNG.nextInt(3);        // 1..3 pixels per update
        this.movingDown = RNG.nextBoolean();    // start either going up or down

        // random delays
        this.startDelay       = RNG.nextInt(60);             // 0–59 frames before moving
        this.pauseFramesAtTop = 15 + RNG.nextInt(45);        // 15–59 frames
        this.pauseFramesAtBottom = 15 + RNG.nextInt(45);     // 15–59 frames
        // =============================================

        this.startY = worldY;
        this.patrolDistancePx = patrolTiles * gp.tileSize;

        // Adjust sprite size
        this.renderWidth  = gp.tileSize * 1;    
        this.renderHeight = gp.tileSize * 2;   

        // Hitbox roughly matches sprite size
        int padding = 6; // adjust this smaller/bigger to tweak hitbox
        solidArea.setBounds(
                padding / 2,
                padding / 2,
                renderWidth  - padding,
                renderHeight - padding
        );
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        loadFrames();
    }
    
    private void loadFrames() {
        try {
            // Load all frame files saw1.png, saw2.png, saw3.png...
            int count = 0;
            while (true) {
                File f = new File("src/assets/sprites/mob/trap/saw" + (count + 1) + ".png");
                if (!f.exists()) break;
                count++;
            }

            if (count == 0) {
                System.err.println("SawTrap: No saw frames found!");
                return;
            }

            frames = new BufferedImage[count];
            for (int i = 0; i < count; i++) {
                frames[i] = ImageIO.read(new File("src/assets/sprites/mob/trap/saw" + (i + 1) + ".png"));
            }

            spritesLoaded = true;
            System.out.println("SawTrap loaded " + count + " frames.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {

        // ==== 1) Handle initial delay (start at different times) ====
        if (startDelayCounter < startDelay) {
            startDelayCounter++;
            // still animate the blade even while waiting
            animate();
            checkPlayerCollision();
            return;
        }

        // ==== 2) Handle pause at endpoints ====
        if (isPaused) {
            pauseCounter++;
            if (pauseCounter >= currentPauseTarget) {
                isPaused = false;
                pauseCounter = 0;
            }
        } else {
            // ==== 3) Normal up/down patrol ====
            if (movingDown) {
                worldY += speed;
                if (worldY >= startY + patrolDistancePx) {
                    worldY = startY + patrolDistancePx;
                    movingDown = false;

                    // pause at bottom with its own random duration
                    isPaused = true;
                    currentPauseTarget = pauseFramesAtBottom;
                    pauseCounter = 0;
                }
            } else {
                worldY -= speed;
                if (worldY <= startY) {
                    worldY = startY;
                    movingDown = true;

                    // pause at top with its own random duration
                    isPaused = true;
                    currentPauseTarget = pauseFramesAtTop;
                    pauseCounter = 0;
                }
            }
        }

        // Animate + collision
        animate();
        checkPlayerCollision();
    }

    private void animate() {
        if (!spritesLoaded) return;

        frameCounter++;
        if (frameCounter > frameSpeed) {
            frameIndex = (frameIndex + 1) % frames.length;
            frameCounter = 0;
        }
    }

    private void checkPlayerCollision() {
        if (gamePanel.player == null) return;

        Rectangle trapBox = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        Rectangle playerBox = new Rectangle(
                gamePanel.player.worldX + gamePanel.player.solidArea.x,
                gamePanel.player.worldY + gamePanel.player.solidArea.y,
                gamePanel.player.solidArea.width,
                gamePanel.player.solidArea.height
        );

        if (trapBox.intersects(playerBox)) {
            gamePanel.player.takeDamage(20);  // damage amount
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gamePanel.cameraX;
        int screenY = worldY - gamePanel.cameraY;

        // Optional: debug hitbox (matches solidArea, not sprite)
        g2.setColor(new Color(255, 0, 0, 80));
        g2.fillRect(
                screenX + solidArea.x,
                screenY + solidArea.y,
                solidArea.width,
                solidArea.height
        );

        if (spritesLoaded) {
            g2.drawImage(frames[frameIndex], screenX, screenY, renderWidth, renderHeight, null);
        }
    }
}
