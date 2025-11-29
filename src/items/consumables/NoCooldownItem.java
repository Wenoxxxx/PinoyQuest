package src.items.consumables;

import src.core.GamePanel;
import src.items.Item;

public class NoCooldownItem extends Item {

    private static final int DURATION_TICKS = 300; // 5 seconds at 60 FPS

    public NoCooldownItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "No Cooldown Potion", "Removes cooldown briefly.");
    }

    @Override
    public void onPickup() {
        gp.player.getSkillManager().activateNoCooldown(DURATION_TICKS);
        this.consumed = true;
    }
}
