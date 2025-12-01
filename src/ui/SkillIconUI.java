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
    //  BACKGROUND SPRITE
    // ================================
    private BufferedImage skillPanel;

    // ================================
    //  PANEL POSITIONING (CUSTOMIZABLE)
    // ================================
    public boolean centerX = true;
    public boolean centerY = false;

    public int posX = 0;          // calculated each draw
    public int posY = 0;

    // HORIZONTAL AND VERTICAL OFFSETS (NEW)
    public int offsetX = -100;     //  MOVE WHOLE PANEL LEFT/RIGHT
    public int offsetY = -5;       //  MOVE WHOLE PANEL UP/DOWN

    // ================================
    //  PANEL SIZE
    // ================================
    public int boxWidth = 250;
    public int boxHeight = 130;

    // ================================
    //  ICON POSITIONS (CUSTOMIZABLE)
    // ================================
    public int iconBaseX = 60;          // where icon 0 starts
    public int iconBaseY = 42;          // vertical base for iconsd
    public int iconSpacingX = 60;       // horizontal spacing between icons

    // Vertical micro-adjustment per icon (NEW!)
    public int[] iconOffsetY = {0, 0, 0};

    // Horizontal micro-adjustment per icon (NEW!)
    public int[] iconOffsetX = {0, 0, 0};

    // ================================
    //  TEXT SPACING CONTROLS
    // ================================
    public int spacingIconToKey = 14;       // icon to key label
    public int spacingKeyToCooldown = 15;   // key to cooldown text

    // Micro adjustments for ALL text (NEW)
    public int textOffsetX = -3;     
    public int textOffsetY = 45;     

    // ================================
    //  FONT SIZES
    // ================================
    public int keyFontSize = 15;
    public int cdFontSize = 10;

    // ================================
    //  SCREEN ANCHORING
    // ================================
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
    //  DRAW UI
    // ==================================
    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // ---------- CENTERING ----------
        if (centerX)
            posX = (screenW / 2) - (boxWidth / 2);

        if (centerY)
            posY = (screenH / 2) - (boxHeight / 2);
        else
            posY = screenH - boxHeight - bottomMargin;

        // ---------- APPLY OFFSETS ----------
        posX += offsetX;
        posY += offsetY;

        // ---------- DRAW BACKGROUND ----------
        if (skillPanel != null)
            g2.drawImage(skillPanel, posX, posY, boxWidth, boxHeight, null);
        else {
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(posX, posY, boxWidth, boxHeight, 15, 15);
        }

        // =============================================
        //  DRAW SKILL LABELS + COOLDOWNS
        // =============================================
        for (int i = 0; i < 3; i++) {

            Skill s = skillManager.getSkill(i);
            if (s == null) continue;

            // ICON POSITION (with H/V micro-adjustments)
            int cx = posX + iconBaseX + (i * iconSpacingX) + iconOffsetX[i];
            int cy = posY + iconBaseY + iconOffsetY[i];

            // ---------------- KEY LABEL ----------------
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, (float) keyFontSize));
            g2.setColor(Color.WHITE);

            String key = gp.keyHandler.getSkillKeyLabel(i);
            int keyW = g2.getFontMetrics().stringWidth(key);

            g2.drawString(
                key,
                cx - keyW / 2 + textOffsetX,
                cy + spacingIconToKey + textOffsetY
            );

            // ---------------- COOLDOWN TEXT ----------------
            String cdText;
            if (s.isActive()) cdText = "Active";
            else if (s.isOnCooldown())
                cdText = String.format("CD %.1fs", s.getCooldownTimer() / 60.0);
            else cdText = "Ready";

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, (float) cdFontSize));
            int cdW = g2.getFontMetrics().stringWidth(cdText);

            g2.drawString(
                cdText,
                cx - cdW / 2 + textOffsetX,
                cy + spacingIconToKey + spacingKeyToCooldown + textOffsetY
            );
        }
    }
}
