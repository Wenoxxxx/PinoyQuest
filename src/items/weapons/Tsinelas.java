package src.items.weapons;

import src.core.GamePanel;
import src.items.Item;

import javax.imageio.ImageIO;
import java.io.File;

public class Tsinelas extends Item {

    public int damage = 8;
    public float attackSpeed = 2.0f;

    public Tsinelas(GamePanel gp, int x, int y) {
        super(gp, x, y, "Tsinelas", "Classic ranged weapon.");

        try {
            sprite = ImageIO.read(new File("src/assets/items/weapons/tsinelas.png"));
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading Tsinelas sprite");
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
