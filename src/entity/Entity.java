package src.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import src.core.GamePanel;

public class Entity {

    // WORLD POSITION
    public int worldX, worldY;
    public int speed;

    // BASIC ANIMATION (for simple entities – player overrides this)
    public BufferedImage up1, up2, up3, up4, up5, up6, down1, left1, right1;
    public String direction;

    // HITBOX
    public final Rectangle solidArea = new Rectangle();
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;

    // ======== GENERIC STATS (SHARED BY MOBS, NPCs, ETC.) ========
    public int maxHealth = 1;
    public int health = 1;

    // ======== OVERRIDABLE BEHAVIOR ========

    // UPDATE ENTITY BEHAVIOR (TO BE OVERRIDDEN)
    public void update() {}

    // DRAW ENTITY SPRITE (TO BE OVERRIDDEN)
    public void draw(Graphics2D g2) {}

    // ======== COLLISION HELPERS ========

    public boolean checkTile(GamePanel gp, int nextWorldX, int nextWorldY) {
        if (gp == null || gp.collision == null) {
            return false;
        }
        if (solidArea.width <= 0 || solidArea.height <= 0) {
            return false;
        }
        return gp.collision.willCollide(this, nextWorldX, nextWorldY);
    }

    public boolean checkTile(GamePanel gp) {
        return checkTile(gp, worldX, worldY);
    }

    // ======== HEALTH HELPERS (OPTIONAL FOR CHILD CLASSES) ========

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.health = this.maxHealth;
    }

    public void takeDamage(int amount) {
        if (amount <= 0) return;
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        health = Math.min(maxHealth, health + amount);
    }

    public boolean isDead() {
        return health <= 0;
    }
}
