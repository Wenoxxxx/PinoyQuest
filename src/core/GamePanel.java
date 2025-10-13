package src.core;

import javax.swing.JPanel;
import java.awt.*;


public class GamePanel extends JPanel{
    
    // SCREEN SETTINGS
    final int mainTileSize = 16;
    final int scale = 3;

    public final int tileSize = mainTileSize * scale; //48x48 pixels
    public final int maxScreenCol = 16; 
    public final int maxScreenRow = 12;

    public final int screenWidth = tileSize * maxScreenCol; //768 px
    public final int screenHeight = tileSize * maxScreenRow; //576 px



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



    // STARTING GAME BY RUNNING GAME LOOP
    public void startGame(){
        gameLoop.start();
    }



    // UPDATES GAME LOGIC EVERY FRAME [MOVEMENT SPEED]
    public void update(){
        // if (keyHandler.upPressed) playerY -= playerSpeed;
        // if (keyHandler.downPressed) playerY += playerSpeed;
        // if (keyHandler.leftPressed) playerX -= playerSpeed;
        // if (keyHandler.rightPressed) playerX += playerSpeed;

        int dx = 0;
        int dy = 0;

        if (keyHandler.upPressed) dy -= 1;
        if (keyHandler.downPressed) dy += 1;
        if (keyHandler.leftPressed) dx -= 1;
        if (keyHandler.rightPressed) dx += 1;

        // If moving diagonally, normalize speed
        if (dx != 0 && dy != 0) {
            playerX += dx * (playerSpeed / Math.sqrt(2));
            playerY += dy * (playerSpeed / Math.sqrt(2));
        } else {
            playerX += dx * playerSpeed;
            playerY += dy * playerSpeed;
        }
    }



    // DRAWS EVERYTHING OM SCREEN EVERY FRAME
    @Override
    public void paintComponent(Graphics g){

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // TEMPORARY CHARACTER 
        g2.setColor(Color.WHITE);
        g2.fillRect(playerX, playerY, tileSize, tileSize);

        g2.dispose();

    }


}
