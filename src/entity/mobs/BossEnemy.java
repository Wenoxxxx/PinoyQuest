package src.entity.mobs;

import src.core.GamePanel;
import src.entity.Player;

import java.awt.*;

public class BossEnemy extends Enemy {

    public BossEnemy(GamePanel gp, int worldX, int worldY, Player player) {
        super(gp, worldX, worldY, player);

        this.maxHealth = 300;
        this.health = maxHealth;

        this.speed = 2;             // faster than normal mobs
        this.attackRange = 42;      // larger attack radius
        this.visionRange = 9999;    // ALWAYS sees player

        this.width = 64;
        this.height = 64;

        this.hitbox = new Rectangle(0, 0, width, height);
    }

    @Override
    public void attack() {
        if (!dead) {
            player.damage(10);
            System.out.println("[BOSS] Smashes the player for 10 damage!");
        }
    }

    @Override
    public void moveTowards(Player p) {
        if (dead) return;

        int dx = p.worldX - worldX;
        int dy = p.worldY - worldY;

        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist == 0) return;

        // Stronger, more intense movement
        double nx = dx / dist;
        double ny = dy / dist;

        // Boss moves more aggressively
        worldX += nx * speed * 1.2;
        worldY += ny * speed * 1.2;
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        g2.setColor(new Color(140, 0, 200)); // purple boss
        g2.fillRect(screenX, screenY, width, height);

        drawHealthBar(g2, screenX, screenY);
    }
}
