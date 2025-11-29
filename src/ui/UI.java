package src.ui;

import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class UI {

    private final GamePanel gp;
    private final Player player;

    private final HudUI hudUI;
    private final InventoryUI inventoryUI;
    private final SkillIconUI skillIconUI;

    public UI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        this.hudUI = new HudUI(gp, player);
        this.inventoryUI = new InventoryUI(gp, player);
        this.skillIconUI = new SkillIconUI(gp, player);
    }
    
    // Getter (needed by GamePanel)
    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    public void draw(Graphics2D g2) {

        // PLAY state → show full HUD and skills
        if (gp.gameState == GamePanel.STATE_PLAY) {
            hudUI.draw(g2);
            skillIconUI.draw(g2);
        }

        // INVENTORY state → draw HUD + Inventory popup
        else if (gp.gameState == GamePanel.STATE_INVENTORY) {

            // Draw HUD normally (health, energy, skills)
            hudUI.draw(g2);
            skillIconUI.draw(g2);

            // Draw the inventory popup OVER the HUD
            inventoryUI.draw(g2);
        }

        // MENU and SETTINGS handled by GamePanel
    }

}
