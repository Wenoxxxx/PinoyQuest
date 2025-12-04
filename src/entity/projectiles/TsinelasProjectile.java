package src.entity.projectiles;

import src.core.GamePanel;
import src.entity.Entity;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TsinelasProjectile extends Entity {

    private final GamePanel gp;
    private final String direction;
    private final int speed = 12;
    private final int damage = 20;
    private int lifetime = 45; // frames
    private boolean expired = false;
    private BufferedImage sprite;

    public TsinelasProjectile(GamePanel gp, int startX, int startY, String direction) {
        this.gp = gp;
        this.direction = direction;
        this.worldX = startX;
        this.worldY = startY;

        solidArea.setBounds(0, 0, gp.tileSize / 2, gp.tileSize / 2);
        loadSprite();
    }

    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("src/assets/items/weapons/tsinelas.png"));
        } catch (Exception e) {
            System.out.println("[Projectile] Failed to load tsinelas sprite.");
        }
    }

    public void update() {
        if (expired) return;

        move();
        lifetime--;
        if (lifetime <= 0) {
            expired = true;
            return;
        }

        Rectangle hitbox = new Rectangle(
                worldX,
                worldY,
                solidArea.width,
                solidArea.height
        );
        if (gp.applyPlayerAttack(hitbox, damage)) {
            expired = true;
        }
    }

    private void move() {
        switch (direction) {
            case "up" -> worldY -= speed;
            case "down" -> worldY += speed;
            case "left" -> worldX -= speed;
            case "right" -> worldX += speed;
        }
    }

    public void draw(Graphics2D g2) {
        if (sprite == null) return;
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;
        g2.drawImage(sprite, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

    public boolean isExpired() {
        return expired;
    }
}

