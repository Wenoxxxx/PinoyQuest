package src.entity;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Entity {

    // x and y has been replaced for the camera movement
    public int worldX, worldY;
    public int speed;

    public BufferedImage up1, up2, up3, up4, up5, up6, down1, left1, right1;
    public String direction;

    // UPDATE ENTITY BEHAVIOR (TO BE OVERIDDEN)
    public void update(){}


    // DRAW ENTITY SPRITE (TO BE OVERIDDEN)
    public void draw(Graphics2D g2){}


    

}