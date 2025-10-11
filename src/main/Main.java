package src.main;

import src.core.GamePanel;



import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        
        //MAIN GAME FRAME 
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        mainFrame.add(gamePanel);
        mainFrame.pack();

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);


        // START THE SEPARATE GAME LOOP
        // gamePanel.startGame();


    }


}
