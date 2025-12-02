package src.items.temporary;

import java.io.File;

import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.items.Item;
public class Map2Key extends Item {

    public Map2Key(GamePanel gp, int x, int y) {
        super(gp, x, y, "Map2 Key", "Unlocks the path to Map 2.");

         // Load sprite
        try {
            sprite = ImageIO.read(
                    new File("src/assets/items/2key.png")
            );
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading no Key sprite");
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
        gp.player.hasMap2Key = true;
        this.consumed = true;
    }
}
