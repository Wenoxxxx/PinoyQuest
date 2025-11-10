package src.main;

import src.core.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        // MAIN GAME FRAME
        JFrame mainFrame = new JFrame("Pinoy Quest");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(true);// SET FALSE FOR FULL SCREEN
        mainFrame.setUndecorated(false);

        // PUTS THE GAME SCREEN INSIDE THE WINDOW
        GamePanel gamePanel = new GamePanel();
        mainFrame.add(gamePanel);
        mainFrame.pack();

        // FULL SCREEN
        mainFrame.setVisible(true);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // START THE SEPARATE GAME LOOP THREAD
        gamePanel.startGame();

        // ICON IMAGE
        String projectRoot = System.getProperty("user.dir");
        String iconPath = projectRoot + File.separator + "assets"
                        + File.separator + "gameIcon"
                        + File.separator + "icon.png";

        ImageIcon icon = new ImageIcon(iconPath);
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        mainFrame.setIconImage(scaledImage);

    }

}