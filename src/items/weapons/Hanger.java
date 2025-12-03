package src.items.weapons;

import src.core.GamePanel;
import src.items.Item;

import javax.imageio.ImageIO;
import java.io.File;

public class Hanger extends Item {

    public int damage = 15;
    public float attackSpeed = 1.2f;

    public Hanger(GamePanel gp, int x, int y) {
        super(gp, x, y, "Hanger", "Melee weapon.");

        try {
            sprite = ImageIO.read(new File("src/assets/items/weapons/hanger.png"));
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading Hanger sprite");
        }

        this.widthTiles = 1;
        this.heightTiles = 1;

        this.pickupWidth  = gp.tileSize;
        this.pickupHeight = gp.tileSize;
    }


    @Override
    public void onPickup() {
        gp.player.weapon = this;
        this.isConsumable = false;
        this.consumed = true;
    }
}
