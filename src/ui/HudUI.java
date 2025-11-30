package src.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;
import src.entity.skills.Skill;
import src.entity.skills.SkillManager;

public class HudUI {

    private final GamePanel gp;
    private final Player player;
    private final SkillManager skillManager;

    public HudUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        this.skillManager = player.getSkillManager();
    }

    public void draw(Graphics2D g2) {
        drawHud(g2);
    }

    public int getHudHeight() {
        return 138; // adjust if your HUD grows later
    }

    private void drawHud(Graphics2D g2) {


        int padding = 28;
        int panelWidth = 320;
        int panelHeight = 140;

        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(padding - 18, padding - 30, panelWidth, panelHeight, 12, 12);

        int barWidth = 240;
        int barHeight = 14;
        int barX = padding;
        int barY = padding;

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.setColor(Color.WHITE);
        g2.drawString("Health", barX, barY - 6);

        drawBar(g2, barX, barY, barWidth, barHeight,
                player.getHealth() / (double) player.getMaxHealth(),
                new Color(210, 82, 82));

        g2.drawString(player.getHealth() + " / " + player.getMaxHealth(),
                barX + barWidth + 12, barY + barHeight - 2);

        int energyY = barY + 32;
        g2.drawString("Energy", barX, energyY - 6);

        drawBar(g2, barX, energyY, barWidth, barHeight,
                player.getEnergy() / (double) player.getMaxEnergy(),
                new Color(80, 165, 220));

        g2.drawString(player.getEnergy() + " / " + player.getMaxEnergy(),
                barX + barWidth + 12, energyY + barHeight - 2);

        drawSkillList(g2, barX, energyY + 36);
    }

    private void drawSkillList(Graphics2D g2, int x, int startY) {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));

        for (int i = 0; i < skillManager.getSlotCount(); i++) {
            Skill skill = skillManager.getSkill(i);
            if (skill == null) continue;

            String key = gp.keyHandler.getSkillKeyLabel(i);
            String label = key + ": " + skill.getName();

            g2.setColor(Color.WHITE);
            g2.drawString(label, x, startY + (i * 18));

            String status;
            if (skill.isActive()) {
                status = "Active";
            } else if (skill.isOnCooldown()) {
                double seconds = skill.getCooldownTimer() / 60.0;
                status = String.format("CD %.1fs", seconds);
            } else if (player.getEnergy() < skill.getEnergyCost()) {
                status = "Need " + skill.getEnergyCost() + " EN";
            } else {
                status = "Ready";
            }

            g2.setColor(new Color(200, 200, 200));
            g2.drawString(status, x + 170, startY + (i * 18));
        }
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height, double percent, Color fillColor) {

        g2.setColor(new Color(45, 45, 45));
        g2.fillRoundRect(x, y, width, height, 8, 8);

        int actualWidth = (int) (width * Math.max(0, Math.min(1, percent)));

        g2.setColor(fillColor);
        g2.fillRoundRect(x, y, actualWidth, height, 8, 8);
    }
}
