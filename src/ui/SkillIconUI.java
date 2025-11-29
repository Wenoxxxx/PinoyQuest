package src.ui;

import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class SkillIconUI {

    private final GamePanel gp;
    private final Player player;

    public SkillIconUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
    }

    public void draw(Graphics2D g2) {
        // In the future:
        // - draw skill icons
        // - apply cooldown overlay shading
        // - show keybind labels
    }
}
