package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.entity.skills.Skill;
import src.entity.skills.SkillManager;

public class SkillIconUI {

    private final GamePanel gp;
    private final Player player;
    private final SkillManager skillManager;

    // ================================
    //  CUSTOMIZABLE SETTINGS
    // ================================

    // Background sprite
    private BufferedImage skillPanel;

    // Position
    public boolean centerX = true;
    public boolean centerY = false;

    public int posX = 0;           // auto-calculated if centered
    public int posY = 0;

    public int offsetX = -100;     // <--- move left(-) or right(+) while centered
    public int offsetY = -5;        // optional vertical offset

    // Scaling
    public int boxWidth = 220;
    public int boxHeight = 130;

    // Icon coordinates INSIDE the panel
    public int iconY = 42;  
    public int[] iconX = { 75, 180, 285 };

    // Text offsets
    public int keyOffsetY = 90;
    public int cdOffsetY  = 112;

    // Font sizes
    public int keyFontSize = 15;
    public int cdFontSize  = 10;

    // Margin from bottom (if not centered vertically)
    public int bottomMargin = 35;

    // ==================================
    //  CONSTRUCTOR
    // ==================================
    public SkillIconUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        this.skillManager = player.getSkillManager();

        try {
            skillPanel = ImageIO.read(new File("src/assets/ui/skill/skillbg.png"));
        } catch (Exception e) {
            System.out.println("ERROR LOADING skillbg.png");
            e.printStackTrace();
        }
    }

    // ==================================
    //  DRAW
    // ==================================
    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth()  > 0 ? gp.getWidth()  : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // -------------------------------
        // POSITIONING
        // -------------------------------

        if (centerX)
            posX = (screenW / 2) - (boxWidth / 2) + offsetX;   // centered + offset
        else
            posX += offsetX;

        if (centerY)
            posY = (screenH / 2) - (boxHeight / 2) + offsetY;
        else
            posY = screenH - boxHeight - bottomMargin + offsetY;

        // -------------------------------
        // DRAW BACKGROUND PANEL
        // -------------------------------
        if (skillPanel != null)
            g2.drawImage(skillPanel, posX, posY, boxWidth, boxHeight, null);
        else {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(posX, posY, boxWidth, boxHeight, 15, 15);
        }

        // -------------------------------
        // DRAW SKILL KEYS + COOLDOWN TEXT
        // -------------------------------

        for (int i = 0; i < 3; i++) {

            Skill s = skillManager.getSkill(i);
            if (s == null) continue;

            int cx = posX + iconX[i];  // center X of the icon

            // Draw KEY (J/K/L)
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) keyFontSize));

            // --- Center key label under icon ---
            String key = gp.keyHandler.getSkillKeyLabel(i);
            int keyWidth = g2.getFontMetrics().stringWidth(key);
            g2.drawString(key, cx - keyWidth / 2, posY + keyOffsetY);

            // ===== Cooldown / Status =====
            String cdText;

            if (s.isActive()) cdText = "Active";
            else if (s.isOnCooldown()) cdText = String.format("CD %.1fs", s.getCooldownTimer() / 60.0);
            else cdText = "Ready";

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, (float) cdFontSize));

            // --- Center status under icon ---
            int cdWidth = g2.getFontMetrics().stringWidth(cdText);
            g2.drawString(cdText, cx - cdWidth / 2, posY + cdOffsetY);
        }

    }
}
