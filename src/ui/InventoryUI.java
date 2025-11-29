package src.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class InventoryUI {

    private final GamePanel gp;
    private final Player player;

    public InventoryUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(100, 100, 400, 300, 18, 18);

        g2.setColor(Color.WHITE);
        g2.drawString("Inventory (WIP)", 120, 130);
    }
}
