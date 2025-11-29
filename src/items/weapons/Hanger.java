package src.items.weapons;

import src.core.GamePanel;
import src.items.Item;
public class Hanger extends Item {

    public Hanger(GamePanel gp, int x, int y) {
        super(gp, x, y, "Hanger", "Melee weapon.");
    }

    @Override
    public void onPickup() {
        gp.player.weapon = this;
        this.consumed = true;
    }
}
