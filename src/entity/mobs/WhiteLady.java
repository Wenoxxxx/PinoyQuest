package src.entity.mobs;

import src.entity.Entity;
import src.core.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class WhiteLady extends Entity {

    private final GamePanel gamePanel;

    // Tracks how many WhiteLadies have been created (for alternating direction)
    private static int spawnCount = 0;

    // Animation frames
    private static final int FRAME_COUNT = 7;
    private final BufferedImage[] frames = new BufferedImage[FRAME_COUNT];

    private int frameIndex = 0;
    private int frameCounter = 0;
    private final int frameSpeed = 6; // lower = faster animation

    private boolean spritesLoaded = false;

    // Patrol logic
    private final int patrolTiles = 23;
    private final int patrolDistancePx;
    private final int leftX;
    private final int rightX;
    private boolean movingRight = true;

    // Sprite render size (in world pixels)
    private final int renderWidth;
    private final int renderHeight;

    private static final String BASE_DIR =
            "src" + File.separator +
            "assets" + File.separator +
            "sprites" + File.separator +
            "mob" + File.separator +
            "whitelady" + File.separator;

    public WhiteLady(GamePanel gp, int worldX, int worldY) {
        this.gamePanel = gp;

        // health from Entity
        this.maxHealth = 50;
        this.health = maxHealth;

        // base patrol range: we treat incoming worldX as the LEFT side
        this.leftX = worldX;
        this.patrolDistancePx = patrolTiles * gp.tileSize;
        this.rightX = leftX + patrolDistancePx;

        // === alternating start position + direction ===
        if (spawnCount % 2 == 0) {
            // even: start left, move right
            this.worldX = leftX;
            this.movingRight = true;
        } else {
            // odd: start right, move left
            this.worldX = rightX;
            this.movingRight = false;
        }
        spawnCount++;
        // ==============================================

        this.worldY = worldY;
        this.speed = 4;

        // ==== SPRITE DRAW SIZE ====
 
        this.renderWidth  = gp.tileSize * 2;   
        this.renderHeight = gp.tileSize * 2;   
        // ==========================

        int hitboxWidth  = renderWidth - 12;             // slightly narrower than sprite
        int hitboxHeight = renderHeight / 3;             // bottom third
        int hitboxX      = (renderWidth - hitboxWidth) / 2; // center horizontally
        int hitboxY      = renderHeight - hitboxHeight - 4; // near bottom

        this.solidArea.setBounds(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
        this.solidAreaDefaultX = this.solidArea.x;
        this.solidAreaDefaultY = this.solidArea.y;
        // ================================

        loadFrames();
    }

    private void loadFrames() {

        System.out.println("\n======== WhiteLady FRAME LOADING ========");

        File folder = new File(BASE_DIR);
        System.out.println("Looking in: " + folder.getAbsolutePath());

        if (!folder.exists()) {
            System.out.println("FOLDER DOES NOT EXIST!");
        } else {
            System.out.println("Folder exists.");
            System.out.println("Files inside folder:");

            File[] list = folder.listFiles();
            if (list != null) {
                for (File f : list) {
                    System.out.println(" - " + f.getName());
                }
            }
        }

        boolean anyLoaded = false;

        for (int i = 0; i < FRAME_COUNT; i++) {
            String fileName = BASE_DIR + "whitelady" + (i + 1) + ".png";

            File file = new File(fileName);
            System.out.println("Loading: " + file.getAbsolutePath() + " exists=" + file.exists());

            if (!file.exists()) {
                frames[i] = null;
                continue;
            }

            try {
                frames[i] = ImageIO.read(file);
                anyLoaded = true;
            } catch (IOException e) {
                System.out.println("Failed to read: " + fileName);
                frames[i] = null;
            }
        }

        spritesLoaded = anyLoaded;

        System.out.println("Sprites Loaded = " + spritesLoaded);
        System.out.println("=========================================\n");
    }

    @Override
    public void update() {
        // Left-right patrol using leftX/rightX
        if (movingRight) {
            worldX += speed;
            if (worldX >= rightX) {
                worldX = rightX;
                movingRight = false;
            }
        } else {
            worldX -= speed;
            if (worldX <= leftX) {
                worldX = leftX;
                movingRight = true;
            }
        }

        // Animation
        if (spritesLoaded) {
            frameCounter++;
            if (frameCounter > frameSpeed) {
                frameCounter = 0;
                frameIndex = (frameIndex + 1) % FRAME_COUNT;
            }
        }

        checkPlayerCollision();
    }

    private void checkPlayerCollision() {
        if (gamePanel.player == null) return;

        Rectangle mobBox = new Rectangle(
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

        if (mobBox.intersects(playerBox)) {
            gamePanel.player.takeDamage(30); // big damage
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gamePanel.cameraX;
        int screenY = worldY - gamePanel.cameraY;

        // // Debug sprite bounds (optional)
        // g2.setColor(new Color(255, 0, 255, 80));
        // g2.fillRect(screenX, screenY, renderWidth, renderHeight);

        // // Debug hitbox (optional)
        // g2.setColor(new Color(0, 255, 0, 120));
        // g2.fillRect(
        //         screenX + solidArea.x,
        //         screenY + solidArea.y,
        //         solidArea.width,
        //         solidArea.height
        // );

        if (!spritesLoaded) return;
        if (frames[frameIndex] == null) return;

        BufferedImage frame = frames[frameIndex];

        if (movingRight) {
            // normal draw (facing right)
            g2.drawImage(frame, screenX, screenY, renderWidth, renderHeight, null);
        } else {
            // flip horizontally (facing left)
            Graphics2D g2d = (Graphics2D) g2;
            java.awt.geom.AffineTransform old = g2d.getTransform();

            g2d.translate(screenX + renderWidth, screenY);
            g2d.scale(-1, 1);
            g2d.drawImage(frame, 0, 0, renderWidth, renderHeight, null);

            g2d.setTransform(old);
        }
    }
}
