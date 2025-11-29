package src.items;

import java.awt.*;
import java.awt.image.BufferedImage;
import src.core.GamePanel;
import src.entity.Player;

public abstract class Item {

    protected GamePanel gp;

    public int worldX;
    public int worldY;

    public BufferedImage sprite;
    public boolean consumed = false;

    public String name;
    public String description;

    public int widthTiles = 1;   // for scaling
    public int heightTiles = 1;

    public Item(GamePanel gp, int x, int y, String name, String description) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.name = name;
        this.description = description;
    }

    // Called when player touches the item
    public abstract void onPickup();

    // NEW ✔ Required by InventoryUI
    // This lets items be used inside the player's inventory
    public void use(Player player) {
        System.out.println("Using base item (no effect): " + name);
    }

    // Whether the player can walk through it
    public boolean isBlocking() { return false; }

    public void draw(Graphics2D g2) {
        if (sprite == null) return;

        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        int drawW = gp.tileSize * widthTiles;
        int drawH = gp.tileSize * heightTiles;

        g2.drawImage(sprite, screenX, screenY, drawW, drawH, null);
    }
}
