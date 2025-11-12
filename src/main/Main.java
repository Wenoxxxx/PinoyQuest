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
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(gamePanel, BorderLayout.CENTER);

        // FULL SCREEN
        mainFrame.setVisible(true);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ICON IMAGE (load before game starts)
        String iconPath = 
                "src" + File.separator + "assets"
                + File.separator + "ui"
                + File.separator + "gameIcon"
                + File.separator + "icon.png";

        File iconFile = new File(iconPath);
        if (iconFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(iconPath);
                if (icon.getImage() != null) {
                    Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    mainFrame.setIconImage(scaledImage);
                }
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + e.getMessage());
            }
        } else {
            System.out.println("Icon not found at: " + iconPath);
        }

        // START THE SEPARATE GAME LOOP THREAD
        gamePanel.startGame();

        
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