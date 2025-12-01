package src.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;
import src.entity.skills.Skill;
import src.entity.skills.SkillManager;

public class SkillIconUI {

    private final GamePanel gp;
    private final Player player;
    private final SkillManager skillManager;

    public int boxWidth = 240;
    public int boxHeight = 70;

    public SkillIconUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        this.skillManager = player.getSkillManager();
    }

    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;  // <-- IMPORTANT FIX

        // === Bottom-Center Position ===
        int x = (screenW / 2) - (boxWidth / 2);
        int y = screenH - boxHeight - 25;    // always visible

        // TEMP DEBUG (to confirm position)
        g2.setColor(new Color(255, 0, 0, 70));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);

        // Actual background
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));

        int textX = x + 20;
        int textY = y + 25;

        for (int i = 0; i < skillManager.getSlotCount(); i++) {
            Skill s = skillManager.getSkill(i);
            if (s == null) continue;

            String key = gp.keyHandler.getSkillKeyLabel(i);
            String label = key + ": " + s.getName();

            g2.drawString(label, textX, textY);
            textY += 20;
        }
    }
}
