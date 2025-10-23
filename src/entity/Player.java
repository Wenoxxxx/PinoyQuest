package src.entity;

import javax.swing.plaf.basic.BasicComboBoxUI.KeyHandler;
import src.core.GamePanel;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Player extends Entity {
    
    GamePanel gamePanel;
    KeyHandler keyHandler;

    public Player(KeyHandler keyHandler){
        this,keyHandler = keyHandler;
        setDefaultValues();
    }

}
