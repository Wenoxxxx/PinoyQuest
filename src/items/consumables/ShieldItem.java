package src.items.consumables;

import java.io.File;

import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;
public class ShieldItem extends Item {

    public ShieldItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "Shield", "Temporary damage immunity.");

        // Load sprite
        try {
            sprite = ImageIO.read(
                    new File("src/assets/items/1sheild.png")
            );
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading no Sheild sprite");
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
        // NO auto-heal
        System.out.println("Picked up: " + name);
    }

    @Override
    public void use(Player player) {
        player.heal(30);
        System.out.println("Used sheild Potion!");
    }
}
