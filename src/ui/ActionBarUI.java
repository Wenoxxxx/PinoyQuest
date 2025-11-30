package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;

public class ActionBarUI {

    private final GamePanel gp;
    private final Player player;

    private BufferedImage barBackground;

    // RESIZE HERE
    private int barWidth = 230;
    private int barHeight = 130;

    private int slotSize = 48;
    private int slotPadding = 12;

    public int activeSlot = 0; // hotbar index 0–2

    public ActionBarUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        try {
            File file = new File("src/assets/ui/actionbar/actionbar.png");
            barBackground = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("ERROR LOADING ACTION BAR BACKGROUND");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {

        // --- Position hotbar DIRECTLY under HUD ---
        int hudY = gp.ui.getHudUI().getHudHeight();
        int barX = 0;
        int barY = hudY + 10; // padding under HUD

        // Draw background panel
        if (barBackground != null) {
            g2.drawImage(barBackground, barX, barY, barWidth, barHeight, null);
        } else {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 15, 15);
        }

        // Hotbar slot layout
        int totalSlotWidth = slotSize * 3 + slotPadding * 2;
        int startX = barX + (barWidth - totalSlotWidth) / 2;
        int startY = barY + (barHeight - slotSize) / 2;

        for (int i = 0; i < 3; i++) {
            int x = startX + i * (slotSize + slotPadding);
            int y = startY;

            // Highlight selected slot
            if (i == activeSlot) {
                g2.setColor(new Color(255, 215, 0, 150));
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
            }

            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

            // Draw item icon (scaled)
            Item item = player.hotbar[i];
            if (item != null && item.sprite != null) {
                g2.drawImage(item.sprite, x + 6, y + 6, slotSize - 12, slotSize - 12, null);
            }

            // Slot number
            g2.drawString("" + (i + 1), x + 4, y + 12);
        }
    }
}
