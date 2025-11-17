package src.tile;

import java.awt.image.BufferedImage;

public class GameObject {

    public String name;
    public BufferedImage image;
    public boolean collision;   // true = blocks player

    public int worldX, worldY;  // pixel position

    // For multi-tile objects (houses, big trees)
    public int width = 1;
    public int height = 1;
}
