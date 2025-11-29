package src.ui;

import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class UI {

    private final GamePanel gp;
    private final Player player;

    private final HudUI hudUI;
    private final InventoryUI inventoryUI;
    private final SkillIconUI skillIconUI;


    public UI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        this.hudUI = new HudUI(gp, player);
        this.inventoryUI = new InventoryUI(gp, player);
        this.skillIconUI = new SkillIconUI(gp, player);

    }

    public void draw(Graphics2D g2) {
        hudUI.draw(g2);

        if (gp.showInventory) {
            inventoryUI.draw(g2);
        }

        skillIconUI.draw(g2);
    }
}
