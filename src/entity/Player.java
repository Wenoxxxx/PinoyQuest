package src.entity;

import src.core.KeyHandler;
import src.core.GamePanel;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


public class Player extends Entity {
    
    GamePanel gamePanel;
    KeyHandler keyHandler;

    // ANIMATION ARRAYS
    public BufferedImage[] upFrames;
    public BufferedImage[] downFrames;
    public BufferedImage[] leftFrames;
    public BufferedImage[] rightFrames;


    public Player(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
        setDefaultValues();
        loadPlayerImages();

    }

    // INITIALIZATION OF PLAYER CHARACTER 
    public void setDefaultValues(){
        
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";//Front default

    }

    // HELPER METHOD TO LOAD A SINGLE IMAGE
    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load: " + path);
            return null;
        }
    }

    // PLAYER SPRITES
    public void loadPlayerImages(){
        
        // Allocate arrays (6 frames each)
        upFrames = new BufferedImage[6];
        downFrames = new BufferedImage[6];
        leftFrames = new BufferedImage[6];
        rightFrames = new BufferedImage[6];


        // Load UP frames
        for (int i = 0; i < 6; i++) {
            upFrames[i] = loadImage("/assets/player/up" + (i + 1) + ".png");
        }

    }

}
