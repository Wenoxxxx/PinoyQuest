package src.items.weapons;

import src.core.GamePanel;
import src.items.Item;

public class Tsinelas extends Item {

    public Tsinelas(GamePanel gp, int x, int y) {
        super(gp, x, y, "Tsinelas", "Classic ranged weapon.");
    }

    @Override
    public void onPickup() {
        gp.player.weapon = this;
        this.consumed = true;
    }
}
