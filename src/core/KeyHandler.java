package src.core;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;


public class KeyHandler implements KeyListener {
    

    public boolean upPressed, downPressed, leftPressed, rightPressed;



    // DETECT TYPED CHARACTERS
    @Override
    public void keyTyped(KeyEvent e){}



    // KEY MOVEMENT:  DETECT IF PRESSED
    @Override
    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();// Get the integer of the pressed key

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;

    }

    // KEY MOVEMENT: DETECT IF RELEASED
    @Override
    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();// Get the integer of the pressed key

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;

    }

    











    
}
