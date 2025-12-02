package src.items.consumables;

import java.io.File;

import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.items.Item;

public class NoCooldownItem extends Item {

    private static final int DURATION_TICKS = 300; // 5 seconds at 60 FPS

    public NoCooldownItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "No Cooldown Potion", "Removes cooldown briefly.");

         // Load sprite
        try {
            sprite = ImageIO.read(
                    new File("src/assets/items/1nocd.png")
            );
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading HealthRegen sprite");
        }

        // Size of item on the map
        this.widthTiles = 2;
        this.heightTiles = 2;

        // Custom pickup hitbox (2× bigger than item)
        this.pickupWidth = gp.tileSize;
        this.pickupHeight = gp.tileSize;
        this.pickupOffsetX = 0;
        this.pickupOffsetY = 0;
    }

    @Override
    public void onPickup() {
        gp.player.getSkillManager().activateNoCooldown(DURATION_TICKS);
        this.consumed = true;
    }
}
