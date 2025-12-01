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

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    public HudUI getHudUI() {
        return hudUI;
    }

    public void draw(Graphics2D g2) {

        if (gp.gameState == GamePanel.STATE_PLAY) {
        hudUI.draw(g2);
        skillIconUI.draw(g2);  // visible
        }
        else if (gp.gameState == GamePanel.STATE_INVENTORY) {
            hudUI.draw(g2);
            inventoryUI.draw(g2);  // draw inventory OVER skills
            // skillIconUI.draw(g2);  <-- REMOVE THIS so skills don't appear during inventory
        }
    }
}
