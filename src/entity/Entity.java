package src.entity;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Entity {
    
    public int x, y;
    public int speed;

    public BufferedImage up1, down1, left1, right1;
    public String direction;

    // UPDATE ENTITY BEHAVIOR (TO BE OVERIDDEN)
    public void update(){}


    // DRAW ENTITY SPRITE (TO BE OVERIDDEN)
    public void draw(Graphics2D g2){}


}
