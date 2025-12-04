package src.entity.mobs;

import src.core.GamePanel;
import src.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DogEnemy extends Enemy {

    private BufferedImage[] walkRight = new BufferedImage[10];
    private BufferedImage[] walkLeft = new BufferedImage[10];

    private int animIndex = 0;
    private int animCounter = 0;

    public DogEnemy(GamePanel gp, int worldX, int worldY, Player player) {
        super(gp, worldX, worldY, player);

        this.maxHealth = 100;
        this.health = maxHealth;

        this.speed = 3;
        this.visionRange = 9999;

        // Scale up to 2x2 tiles (96x96 pixels)
        this.width = gp.tileSize * 2;
        this.height = gp.tileSize * 2;
        this.attackRange = (int)(28 * 2.0f); // Scale attack range proportionally

        this.hitbox = new Rectangle(0, 0, width, height);

        loadWalkSprites();
    }

    private void loadWalkSprites() {
        String basePath = "src" + File.separator + 
                         "assets" + File.separator + 
                         "sprites" + File.separator + 
                         "mob" + File.separator + 
                         "dog" + File.separator;

        System.out.println("\n======== DogEnemy FRAME LOADING ========");
        System.out.println("Base path: " + basePath);

        for (int i = 0; i < 10; i++) {
            String rightFileName = basePath + "walkR" + (i + 1) + ".png";
            String leftFileName = basePath + "walkL" + (i + 1) + ".png";

            try {
                File rightFile = new File(rightFileName);
                File leftFile = new File(leftFileName);

                if (rightFile.exists()) {
                    walkRight[i] = ImageIO.read(rightFile);
                } else {
                    System.out.println("File not found: " + rightFileName);
                    walkRight[i] = null;
                }

                if (leftFile.exists()) {
                    walkLeft[i] = ImageIO.read(leftFile);
                } else {
                    System.out.println("File not found: " + leftFileName);
                    walkLeft[i] = null;
                }

                System.out.println("Frame " + (i + 1) + 
                        " | R=" + (walkRight[i] != null) + 
                        " | L=" + (walkLeft[i] != null));
            } catch (IOException e) {
                System.out.println("Failed to load frame " + (i + 1) + ": " + e.getMessage());
                walkRight[i] = null;
                walkLeft[i] = null;
            }
        }
        System.out.println("=========================================\n");
    }

    @Override
    public void attack() {
        if (!dead) {
            player.damage(4);
            System.out.println("[DOG] Bites player!");
        }
    }

    @Override
    public void moveTowards(Player p) {
        if (dead) return;

        int dx = p.worldX - worldX;
        int dy = p.worldY - worldY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return;

        double nx = dx / dist;
        double ny = dy / dist;

        worldX += nx * speed;
        worldY += ny * speed;

        // Animation speed
        animCounter++;
        if (animCounter >= 4) {
            animCounter = 0;
            animIndex++;
            if (animIndex >= 10) animIndex = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        BufferedImage frame;

        // Choose facing direction
        if (player.worldX < worldX) {
            frame = walkLeft[animIndex];   // Face left
        } else {
            frame = walkRight[animIndex];  // Face right
        }

        // Only draw if sprite loaded successfully
        if (frame != null) {
            g2.drawImage(frame, screenX, screenY, width, height, null);
        } else {
            // Fallback: draw a colored rectangle if sprite failed to load
            g2.setColor(Color.YELLOW);
            g2.fillRect(screenX, screenY, width, height);
        }
        drawHealthBar(g2, screenX, screenY);
    }
}

