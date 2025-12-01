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

    private int barWidth = 230;
    private int barHeight = 130;

    private int slotSize = 48;
    private int slotPadding = 12;

    public int activeSlot = 0;

    public ActionBarUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        try {
            barBackground = ImageIO.read(new File("src/assets/ui/actionbar/actionbar.png"));
        } catch (Exception e) {
            System.out.println("ERROR LOADING ACTION BAR BACKGROUND");
            e.printStackTrace();
        }
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        int skillsWidth = gp.ui.getSkillIconUI().boxWidth;
        int totalGroupWidth = skillsWidth + 30 + barWidth;

        // LEFT boundary of the combined group
        int startX = (screenW - totalGroupWidth) / 2;

        // Y position
        int y = screenH - barHeight - 40;

        // Action bar goes to the RIGHT of skill box
        int barX = startX + skillsWidth + 30;

        // Draw background
        if (barBackground != null) {
            g2.drawImage(barBackground, barX, y, barWidth, barHeight, null);
        } else {
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(barX, y, barWidth, barHeight, 15, 15);
        }

        // Slot layout
        int totalSlotWidth = slotSize * 3 + slotPadding * 2;
        int startSlotX = barX + (barWidth - totalSlotWidth) / 2;
        int startSlotY = y + (barHeight - slotSize) / 2;

        for (int i = 0; i < 3; i++) {

            int x = startSlotX + i * (slotSize + slotPadding);
            int slotY = startSlotY;

            // Highlight active slot
            if (i == activeSlot) {
                g2.setColor(new Color(255, 215, 0, 150));
                g2.fillRoundRect(x, slotY, slotSize, slotSize, 10, 10);
            }

            // Border
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(x, slotY, slotSize, slotSize, 10, 10);

            // Item icon
            Item item = player.hotbar[i];
            if (item != null && item.sprite != null) {
                g2.drawImage(item.sprite, x + 6, slotY + 6, slotSize - 12, slotSize - 12, null);
            }

            // Slot number
            g2.drawString("" + (i + 1), x + 4, slotY + 12);
        }
    }
}
