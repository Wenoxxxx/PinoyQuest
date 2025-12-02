package src.items.consumables;

import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;

public class ShieldItem extends Item {

    private static final long DURATION_MS = 5000; // 5 seconds shield

    public ShieldItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "Shield", "Temporary damage immunity.");

        try {
            sprite = ImageIO.read(
                    new File("src/assets/items/1sheild.png")
            );
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading Shield sprite");
        }

        this.widthTiles = 2;
        this.heightTiles = 2;

        this.pickupWidth = gp.tileSize;
        this.pickupHeight = gp.tileSize;
        this.pickupOffsetX = 0;
        this.pickupOffsetY = 0;
    }

    @Override
    public void onPickup() {
        System.out.println("Picked up: " + name);
    }

    @Override
    public void use(Player player) {

        System.out.println("[ITEM] Shield activated for " + DURATION_MS + " ms!");

        player.enableShield(DURATION_MS);

        consumed = true;
    }
}
