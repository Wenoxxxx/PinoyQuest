package src.items.consumables;

import src.items.Item;
import src.core.GamePanel;

public class HealthRegenItem extends Item {

    public HealthRegenItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "Health Potion", "Restores 30 HP.");
    }

    @Override
    public void onPickup() {
        gp.player.heal(30);
        this.consumed = true;
    }
}
