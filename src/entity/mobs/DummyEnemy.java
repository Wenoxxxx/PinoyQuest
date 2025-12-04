package src.entity.mobs;

import java.awt.Color;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class DummyEnemy extends Enemy {

    public DummyEnemy(GamePanel gp, int worldX, int worldY, Player player) {
        super(gp, worldX, worldY, player);

        this.speed = 2;
        this.visionRange = 99999;   // Always see player for testing
        this.attackRange = 32;
    }

    @Override
    public void attack() {
        if (!dead) {
            System.out.println("[DummyEnemy] Attacks player!");
            player.damage(5);
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        // Convert world -> screen (camera transform)
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        // Draw only when inside viewport
        if (screenX >= -32 && screenX <= gp.screenWidth &&
            screenY >= -32 && screenY <= gp.screenHeight) {

            g2.setColor(Color.RED);
            g2.fillRect(screenX, screenY, 32, 32);
        }

         drawHealthBar(g2, screenX, screenY);
    }
}
