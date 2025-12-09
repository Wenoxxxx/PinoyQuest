package src.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;

public class HudUI {

    private final GamePanel gp;
    private final Player player;

    // ==== HUD BACKGROUND SPRITE ====
    private BufferedImage hudPanel;
    public int hudScale = 4;       // resize HUD sprite
    public int hudX = 0;
    public int hudY = 30;

    // ==== CUSTOMIZABLE BAR SETTINGS ====
    public int barXOffset = 100;
    public int barYOffsetHealth = 28;
    public int barYOffsetEnergy = 58;
    public int barWidth = 170;
    public int barHeight = 10;
    public int barCornerRadius = 6;

    public Color healthColor = new Color(210, 82, 82);
    public Color energyColor = new Color(80, 165, 220);

    public HudUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        // Load HUD sprite
        try {
            hudPanel = ImageIO.read(new File("assets/ui/hud/hud_panel.png"));
        } catch (Exception e) {
            System.out.println("ERROR LOADING HUD PANEL!");
            e.printStackTrace();
        }

        applyPresetForYourSprite();
    }

    // Auto-fit layout for your HUD sprite
    private void applyPresetForYourSprite() {
        barXOffset = 130;
        barYOffsetHealth = 28;
        barYOffsetEnergy = 58;
        barWidth = 140;
        barHeight = 10;
        barCornerRadius = 6;
    }

    public void draw(Graphics2D g2) {
        drawHud(g2);
    }

    // Used by ActionBarUI to position hotbar below HUD
    public int getHudHeight() {
        if (hudPanel != null)
            return hudPanel.getHeight() * hudScale + 20;
        return 140;
    }

    private void drawHud(Graphics2D g2) {

        // === Draw Background Sprite ===
        if (hudPanel != null) {
            int scaledW = hudPanel.getWidth() * hudScale;
            int scaledH = hudPanel.getHeight() * hudScale;
            g2.drawImage(hudPanel, hudX, hudY, scaledW, scaledH, null);
        } else {
            // fallback box
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(hudX, hudY, 320, 140, 12, 12);
        }

        // === Positions ===
        int barX = hudX + barXOffset+ 30;
        int healthY = hudY + barYOffsetHealth+ 10;
        int energyY = hudY + barYOffsetEnergy+ 10;

        // === HEALTH BAR ===
        drawBar(g2, barX, healthY, barWidth, barHeight,
                player.getHealth() / (double) player.getMaxHealth(),
                healthColor);

        // === ENERGY BAR ===
        drawBar(g2, barX, energyY, barWidth, barHeight,
                player.getEnergy() / (double) player.getMaxEnergy(),
                energyColor);

        // === LABELS ===
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(Color.WHITE);

        g2.drawString("HP", barX - 28, healthY + 12);
        g2.drawString("EN", barX - 28, energyY + 12);
    }

    // Simple Rounded Bar Renderer
    private void drawBar(Graphics2D g2, int x, int y, int width, int height,
                         double percent, Color fillColor) {

        // background
        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(x, y, width, height, barCornerRadius, barCornerRadius);

        // fill
        int filled = (int) (width * Math.max(0, Math.min(1, percent)));

        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, filled, height, barCornerRadius, barCornerRadius);
    }
}
