package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;

public class GameOverUI {

    private final GamePanel gp;

    private BufferedImage popupBackground;
    private BufferedImage[] buttonSprites = new BufferedImage[2];

    // Button scale identical to MainMenu
    private final double buttonScale = 5.5;

    // Selected button (0 = restart, 1 = quit)
    private int selectedIndex = 0;

    public GameOverUI(GamePanel gp) {
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        try {
            popupBackground = ImageIO.read(new File("src/assets/ui/gameover/gameOverBg.png"));

            buttonSprites[0] = ImageIO.read(new File("src/assets/ui/gameover/gameOverBtn.png"));      // Restart
            buttonSprites[1] = ImageIO.read(new File("src/assets/ui/gameover/gameOverQuitBtn.png")); // Quit

        } catch (Exception e) {
            System.out.println("[GameOverUI] Failed to load assets!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // Dim background like a modal
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, screenW, screenH);

        // Draw the game over banner image (centered)
        drawPopup(g2, screenW, screenH);

        // Draw the restart & quit buttons using MainMenu logic
        drawButtons(g2, screenW, screenH);
    }

    private void drawPopup(Graphics2D g2, int screenW, int screenH) {

        if (popupBackground != null) {
            int rawW = popupBackground.getWidth();
            int rawH = popupBackground.getHeight();

            double scale = 4.0;
            int drawW = (int) (rawW * scale);
            int drawH = (int) (rawH * scale);

            int x = (screenW - drawW) / 2;
            int y = (screenH - drawH) / 2 - 250;

            g2.drawImage(popupBackground, x, y, drawW, drawH, null);
        }
    }

    private void drawButtons(Graphics2D g2, int screenW, int screenH) {

        int defaultButtonWidth = 260;
        int defaultButtonHeight = 60;

        int centerX = screenW / 2;
        int gap = 5;

        // Base height identical to main menu
        int baseButtonHeight = defaultButtonHeight;
        if (buttonSprites[0] != null) {
            baseButtonHeight = (int) (buttonSprites[0].getHeight() * buttonScale);
        }

        // Start drawing below popup
        int startY = screenH / 2 + 80; // adjust based on popup position

        for (int i = 0; i < 2; i++) {

            BufferedImage btnImg = buttonSprites[i];

            int rawW = btnImg != null ? btnImg.getWidth() : defaultButtonWidth;
            int rawH = btnImg != null ? btnImg.getHeight() : defaultButtonHeight;

            int buttonWidth = (int) (rawW * buttonScale);
            int buttonHeight = (int) (rawH * buttonScale);

            int x = centerX - (buttonWidth / 2);
            int y = startY + i * (baseButtonHeight + gap);

            // Draw button sprite
            g2.drawImage(btnImg, x, y, buttonWidth, buttonHeight, null);

            // // Highlight identical to main menu
            // if (i == selectedIndex) {
            //     g2.setColor(Color.YELLOW);
            //     g2.setStroke(new BasicStroke(3));
            //     g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            // }
        }
    }

    // ===== INPUT =====
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + 2) % 2;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % 2;
    }

    public void select() {

        if (selectedIndex == 0) {
            gp.startNewGame();  // Restart the game
        }

        else if (selectedIndex == 1) {
            System.exit(0);     // Quit the whole game
        }
    }
}
