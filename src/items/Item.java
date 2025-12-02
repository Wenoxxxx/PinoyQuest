package src.items;

import java.awt.*;
import java.awt.image.BufferedImage;
import src.core.GamePanel;
import src.entity.Player;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public abstract class Item {

    protected GamePanel gp;

    public int worldX;
    public int worldY;

    public BufferedImage sprite;
    public boolean consumed = false;

    public String name;
    public String description;

    // Visual draw size (in tiles)
    public int widthTiles = 1;
    public int heightTiles = 1;

    // New: custom draw adjustments
    public int drawOffsetX = -10;   // pixel offset inside tile
    public int drawOffsetY = -10;   // pixel offset inside tile
    public float drawScale = 1.0f; // 1.0 = full tile size

    // Pickup hitbox
    public int pickupWidth = 0;
    public int pickupHeight = 0;
    public int pickupOffsetX = 0;
    public int pickupOffsetY = 0;

    public Item(GamePanel gp, int x, int y, String name, String description) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.name = name;
        this.description = description;
    }

    // Clone items from template
    public Item copy(GamePanel gp, int worldX, int worldY) {
        try {
            Item newItem = this.getClass()
                    .getDeclaredConstructor(GamePanel.class, int.class, int.class)
                    .newInstance(gp, worldX, worldY);

            // Copy over customization
            newItem.drawOffsetX = this.drawOffsetX;
            newItem.drawOffsetY = this.drawOffsetY;
            newItem.drawScale   = this.drawScale;

            newItem.pickupWidth = this.pickupWidth;
            newItem.pickupHeight = this.pickupHeight;
            newItem.pickupOffsetX = this.pickupOffsetX;
            newItem.pickupOffsetY = this.pickupOffsetY;

            return newItem;

        } catch (Exception e) {
            System.out.println("[ITEM ERROR] Cannot copy item: " + this.getClass());
            e.printStackTrace();
            return null;
        }
    }

    public abstract void onPickup();

    public void use(Player player) {
        System.out.println("Using base item: " + name);
    }

    public boolean isBlocking() { return false; }


    // ===========================================================
    // CUSTOMIZABLE DRAW SYSTEM
    // ===========================================================
    public void draw(Graphics2D g2) {
        if (sprite == null) return;

        int tile = gp.tileSize;

        // world  screen
        int screenX = worldX - gp.cameraX;
        int screenY = worldY - gp.cameraY;

        // base size
        int baseW = tile * widthTiles;
        int baseH = tile * heightTiles;

        // scale the item
        int drawW = (int)(baseW * drawScale);
        int drawH = (int)(baseH * drawScale);

        // center inside tile
        int centerX = screenX + (tile - drawW) / 2;
        int centerY = screenY + (tile - drawH) / 2;

        // apply custom offsets
        int finalX = centerX + drawOffsetX;
        int finalY = centerY + drawOffsetY;

        g2.drawImage(sprite, finalX, finalY, drawW, drawH, null);
    }


    // ===========================================================
    // SAFER RESOURCE LOADER
    // ===========================================================
    protected BufferedImage loadSprite(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("[ITEM ERROR] Missing sprite: " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
