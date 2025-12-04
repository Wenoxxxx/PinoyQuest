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

    // === NEW: GAME OVER UI ===
    private final GameOverUI gameOverUI;

    // ========== SIMPLE MESSAGE SYSTEM ==========
    private String message = "";
    private int messageTimer = 0;
    private static final int MESSAGE_DURATION = 90;

    public UI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        this.hudUI = new HudUI(gp, player);
        this.inventoryUI = new InventoryUI(gp, player);
        this.skillIconUI = new SkillIconUI(gp, player);

        // NEW
        this.gameOverUI = new GameOverUI(gp);
    }

    // ======= UI GETTERS =======
    public InventoryUI getInventoryUI() { return inventoryUI; }
    public HudUI getHudUI() { return hudUI; }
    public SkillIconUI getSkillIconUI() { return skillIconUI; }

    // NEW: GameOverUI Getter
    public GameOverUI getGameOverUI() { return gameOverUI; }

    // =========================================================
    //                  PUBLIC MESSAGE API
    // =========================================================
    public void showMessage(String msg) {
        this.message = msg;
        this.messageTimer = MESSAGE_DURATION;
    }

    // =========================================================
    //                  MESSAGE DRAWER
    // =========================================================
    private void drawMessage(Graphics2D g2) {
        if (messageTimer > 0) {

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 10f));

            int textWidth = g2.getFontMetrics().stringWidth(message);

            int screenW = gp.getWidth();
            int screenH = gp.getHeight();

            int barHeight = 120;
            int offsetY = -58;

            int msgX = (screenW / 2) - (textWidth / 2);
            int msgY = screenH - barHeight + offsetY;

            int pad = 10;
            int boxW = textWidth + pad * 2;
            int boxH = 28;

            int boxX = msgX - pad;
            int boxY = msgY - 18;

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(boxX, boxY, boxW, boxH, 10, 10);

            g2.setColor(Color.WHITE);
            g2.drawString(message, msgX, msgY);

            messageTimer--;
        }
    }

    // =========================================================
    //                  MAIN DRAW FUNCTION
    // =========================================================
    public void draw(Graphics2D g2) {

        // === GAME OVER SCREEN ===
        if (gp.gameState == GamePanel.STATE_GAME_OVER) {
            gameOverUI.draw(g2);
            return; // Skip HUD / Inventory / Skills
        }

        // === PLAYING ===
        if (gp.gameState == GamePanel.STATE_PLAY) {
            hudUI.draw(g2);
            skillIconUI.draw(g2);
        }

        // === INVENTORY ===
        else if (gp.gameState == GamePanel.STATE_INVENTORY) {
            hudUI.draw(g2);
            inventoryUI.draw(g2);
        }

        // Draw message system
        drawMessage(g2);
    }
}
