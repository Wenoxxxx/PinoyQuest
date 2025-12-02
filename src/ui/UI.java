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

            // ===== MESSAGE FONT LIKE SKILLS =====
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 10f));

            // Text width for centering
            int textWidth = g2.getFontMetrics().stringWidth(message);

            // ======= CUSTOM POSITIONING =======
            int screenW = gp.getWidth();
            int screenH = gp.getHeight();

            // Position above action bar and skills
            int barHeight = 120;             // height area of skills + action bar
            int offsetY = -58;               // manual tweak  move higher/lower

            int msgX = (screenW / 2) - (textWidth / 2);
            int msgY = screenH - barHeight + offsetY;

            // Background padding
            int pad = 10;
            int boxW = textWidth + pad * 2;
            int boxH = 28;

            int boxX = msgX - pad;
            int boxY = msgY - 18;

            // ===== BACKGROUND =====
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 10, 10);

            // ===== TEXT =====
            g2.setColor(Color.WHITE);
            g2.drawString(message, msgX, msgY);

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
