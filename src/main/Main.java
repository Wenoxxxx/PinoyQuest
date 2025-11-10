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

        
        //RUNNING INDICATOR
        boolean[] isRunning = {true}; // Use array so it can be modified inside lambda

        Thread indicator = new Thread(() -> {
            while (isRunning[0]) {
                System.out.println("PINOYQUEST: RUNNING");
                try { Thread.sleep(1000); } catch (Exception ignored) {}
            }
            System.out.println("PINOYQUEST: GAME ENDED");
        });

        // Add shutdown hook: runs when JFrame closes or Ctrl+C is pressed
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            isRunning[0] = false;
            try { Thread.sleep(200); } catch (Exception ignored) {} // short wait for clean exit
            System.out.println("PINOYQUEST: GAME ENDED");
        }));

        indicator.start();

    }

}