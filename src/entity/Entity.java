package src.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import src.core.GamePanel;

public class Entity {

    // x and y has been replaced for the camera movement
    public int worldX, worldY;
    public int speed;

    public BufferedImage up1, up2, up3, up4, up5, up6, down1, left1, right1;
    public String direction;

    // HITBOX
    public final Rectangle solidArea = new Rectangle();
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;

    // UPDATE ENTITY BEHAVIOR (TO BE OVERIDDEN)
    public void update(){}


    // DRAW ENTITY SPRITE (TO BE OVERIDDEN)
    public void draw(Graphics2D g2){}

    public boolean checkTile(GamePanel gp, int nextWorldX, int nextWorldY) {
        if (gp == null || gp.collision == null) {
            return false;
        }
        if (solidArea.width <= 0 || solidArea.height <= 0) {
            return false;
        }
        return gp.collision.willCollide(this, nextWorldX, nextWorldY);
    }

    public boolean checkTile(GamePanel gp) {
        return checkTile(gp, worldX, worldY);
    }

}