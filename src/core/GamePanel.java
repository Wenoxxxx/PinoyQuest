package src.core;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

public class GamePanel extends JPanel{
    
    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale; //48x48 pixels
    public final int maxScreenCol = 16; 
    public final int maxScreenRow = 12;

    public final int screenWidth = tileSize * maxScreenCol; //768 px
    public final int screenHeight = tileSize * maxScreenRow; //576 px



    // SYSTEM
    Thread gameThread;
    int FPS = 60;



    // [TEST] PLAYER POSITION
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;


    
    // KEY INPUT (imported from KeyHandler.java)
    KeyHandler keyHandler = new KeyHandler();


    // GAME LOOP OBJECT
    private GameLoop gameLoop;

    // CONSTRUCTOR
    public GamePanel (){
        
        // BASIC SETUP
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));//setting the size of game window
        this.setBackground(Color.BLACK);//Background color of the panel
        this.setDoubleBuffered(true); // Enable double buffering to prevent flickering
        this.addKeyListener(keyHandler); // Add the keyboard listener (so it detects key presses)
        this.setFocusable(true); // Makes sure the panel can receive keyboard input

        // INITIALIZING GAME LOOP
        gameLoop = new GameLoop(this);

    }





}
