package src.tile;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class GameObject {

    public String name;
    public BufferedImage image;

    // true = blocks player, false = walkable / decorative
    public boolean collision = false;

    // world position in pixels (top-left of the sprite)
    public int worldX, worldY;

    // size in tiles (used for drawing & default hitbox)
    public int width = 1;
    public int height = 1;

    // Collision hitbox (relative to worldX/worldY)
    // ObjectManager will set the proper size in loadObjectTypes()
    public Rectangle solidArea = new Rectangle(0, 0, 0, 0);
}
