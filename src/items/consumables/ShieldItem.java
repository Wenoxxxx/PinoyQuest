package src.items.consumables;

import src.core.GamePanel;
import src.items.Item;
public class ShieldItem extends Item {

    public ShieldItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "Shield", "Temporary damage immunity.");
    }

    @Override
    public void onPickup() {
        gp.player.activateShield(5 * 60);
        this.consumed = true;
    }
}
