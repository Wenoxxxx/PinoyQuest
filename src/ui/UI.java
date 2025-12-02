package src.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class UI {

    private final GamePanel gp;
    private final Player player;

    private final HudUI hudUI;
    private final InventoryUI inventoryUI;
    private final SkillIconUI skillIconUI;

    // ========== SIMPLE MESSAGE SYSTEM ==========
    private String message = "";
    private int messageTimer = 0;
    private static final int MESSAGE_DURATION = 90; // ~1.5 sec

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

    public SkillIconUI getSkillIconUI() {
        return skillIconUI;
    }

    // =========================================================
    //        PUBLIC MESSAGE API
    // =========================================================
    public void showMessage(String msg) {
        this.message = msg;
        this.messageTimer = MESSAGE_DURATION;
    }

    // =========================================================
    //        INTERNAL MESSAGE DRAWING
    // =========================================================
    private void drawMessage(Graphics2D g2) {
        if (messageTimer > 0) {

            // Background box
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(20, 20, 260, 40, 10, 10);

            // Text
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
            g2.drawString(message, 30, 47);

            messageTimer--;
        }
    }

    // =========================================================
    //        MAIN DRAW FUNCTION
    // =========================================================
    public void draw(Graphics2D g2) {

        if (gp.gameState == GamePanel.STATE_PLAY) {
            hudUI.draw(g2);
            skillIconUI.draw(g2);
        }
        else if (gp.gameState == GamePanel.STATE_INVENTORY) {
            hudUI.draw(g2);
            inventoryUI.draw(g2);
        }

        // Draw pickup messages for BOTH states
        drawMessage(g2);
    }
}
