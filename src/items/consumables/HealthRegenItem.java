package src.items.consumables;

import src.items.Item;
import src.core.GamePanel;

import javax.imageio.ImageIO;
import java.io.File;

public class HealthRegenItem extends Item {

    public HealthRegenItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "Health Potion", "Restores 30 HP.");

        // Load sprite (32x32)
        try {
            sprite = ImageIO.read(new File("src/assets/items/1healthregen.png"));
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading HealthRegen sprite");
        }

        // Item should be EXACTLY 1 tile big (48x48 when drawn)
        this.widthTiles = 2;
        this.heightTiles = 2;

        // Pickup hitbox = 1 tile
        this.pickupWidth = gp.tileSize;
        this.pickupHeight = gp.tileSize;

        // Center pickup hitbox on item
        this.pickupOffsetX = 0;
        this.pickupOffsetY = 0;
    }

    @Override
    public void onPickup() {
        gp.player.heal(30);
        this.consumed = true;
    }
}
