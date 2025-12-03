package src.items.weapons;

import java.io.File;
import javax.imageio.ImageIO;
import src.core.GamePanel;
import src.items.Item;

public class Hanger extends Item {

    public Hanger(GamePanel gp, int x, int y) {
        super(gp, x, y, "hanger", "Melee weapon.");

        // Load sprite from movementItemHanger folder
        try {
            sprite = ImageIO.read(new File("src/assets/sprites/player/movementItemHanger/hanger.png"));
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading Hanger sprite");
            e.printStackTrace();
        }

        // Size of item on the map
        this.widthTiles = 2;
        this.heightTiles = 2;

        // Pickup hitbox
        this.pickupWidth = gp.tileSize;
        this.pickupHeight = gp.tileSize;
        this.pickupOffsetX = 0;
        this.pickupOffsetY = 0;
    }

    @Override
    public void onPickup() {
        // Only allow pickup on map 2 (currentMap == 1, since maps are 0-indexed)
        if (gp.currentMap != 1) {
            System.out.println("[Hanger] Can only be picked up on map 2!");
            return;
        }

        gp.player.weapon = this;
        this.consumed = true;
        System.out.println("[Hanger] Equipped! You can now use attack animations on all maps.");
    }
}
