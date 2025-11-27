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
    private final int startX;
    private boolean movingRight = true;

    private static final String BASE_DIR =
            "src" + File.separator +
            "assets" + File.separator +
            "sprites" + File.separator +
            "mob" + File.separator +
            "whitelady" + File.separator;

    public WhiteLady(GamePanel gp, int worldX, int worldY) {
        this.gamePanel = gp;

        this.worldX = worldX;
        this.worldY = worldY;

        this.speed = 4;

        // health from Entity
        this.maxHealth = 50;
        this.health = maxHealth;

        // patrol range
        this.startX = worldX;
        this.patrolDistancePx = patrolTiles * gp.tileSize;

        // hitbox
        this.solidArea.setBounds(4, 4, gp.tileSize - 15, gp.tileSize - 15);
        this.solidAreaDefaultX = this.solidArea.x;
        this.solidAreaDefaultY = this.solidArea.y;

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
        // Simple left-right patrol
        if (movingRight) {
            worldX += speed;
            if (worldX >= startX + patrolDistancePx) {
                worldX = startX + patrolDistancePx;
                movingRight = false;
            }
        } else {
            worldX -= speed;
            if (worldX <= startX) {
                worldX = startX;
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

        int size = gamePanel.tileSize * 2;

        // Debug magenta box so you always see where she is
        g2.setColor(new Color(255, 0, 255, 80));
        g2.fillRect(screenX, screenY, size, size);

        if (!spritesLoaded) return;
        if (frames[frameIndex] == null) return;

        BufferedImage frame = frames[frameIndex];

        if (movingRight) {
            // normal draw (facing right)
            g2.drawImage(frame, screenX, screenY, size, size, null);
        } else {
            // flip horizontally (facing left)
            Graphics2D g2d = (Graphics2D) g2;
            java.awt.geom.AffineTransform old = g2d.getTransform();

            g2d.translate(screenX + size, screenY);
            g2d.scale(-1, 1);
            g2d.drawImage(frame, 0, 0, size, size, null);

            g2d.setTransform(old);
        }
    }
}
