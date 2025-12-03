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

    // ======================= CUSTOMIZATION =======================

    // Popup background
    public double popupScale = 4.2;     // ★ lowered so it no longer overlaps buttons
    public int popupOffsetX = 0;
    public int popupOffsetY = -120;     // ★ slightly lifted

    // Buttons
    public double buttonScale = 5.0;    // ★ slightly smaller so two buttons fit nicely
    public int buttonOffsetX = 0;
    public int buttonOffsetY = 80;      // ★ moved LOWER to reveal Quit button

    private int selectedIndex = 0;

    public GameOverUI(GamePanel gp) {
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        try {
            popupBackground = ImageIO.read(new File("src/assets/ui/gameover/gameOverBg.png"));

            File retryFile = new File("src/assets/ui/gameover/gameOverBtn.png");
            File quitFile = new File("src/assets/ui/gameover/gameOverQuitBtn.png");

            buttonSprites[0] = ImageIO.read(retryFile);
            buttonSprites[1] = ImageIO.read(quitFile);

            System.out.println("[GameOverUI] Retry exists: " + retryFile.exists());
            System.out.println("[GameOverUI] Quit exists: " + quitFile.exists());

        } catch (Exception e) {
            System.out.println("[GameOverUI] Failed to load assets!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // Dim screen like menu
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, screenW, screenH);

        drawPopup(g2, screenW, screenH);
        drawButtons(g2, screenW, screenH);
    }

    // ========================== POPUP BG ==========================
    private void drawPopup(Graphics2D g2, int screenW, int screenH) {

        if (popupBackground != null) {

            int rawW = popupBackground.getWidth();
            int rawH = popupBackground.getHeight();

            int drawW = (int) (rawW * popupScale);
            int drawH = (int) (rawH * popupScale);

            int x = (screenW - drawW) / 2 + popupOffsetX;
            int y = (screenH - drawH) / 2 + popupOffsetY;

            g2.drawImage(popupBackground, x, y, drawW, drawH, null);
        }
    }

    // ========================== BUTTONS ==========================
    private void drawButtons(Graphics2D g2, int screenW, int screenH) {

        int defaultButtonWidth = 260;
        int defaultButtonHeight = 60;

        int centerX = screenW / 2;
        int gap = 5;

        // Determine button height based on scale
        int baseButtonHeight = defaultButtonHeight;
        if (buttonSprites[0] != null) {
            baseButtonHeight = (int) (buttonSprites[0].getHeight() * buttonScale);
        }

        // NEW Y layout – pushes BOTH buttons into view
        int startY = (screenH / 2) + buttonOffsetY;

        for (int i = 0; i < 2; i++) {

            BufferedImage btn = buttonSprites[i];

            int rawW = btn != null ? btn.getWidth() : defaultButtonWidth;
            int rawH = btn != null ? btn.getHeight() : defaultButtonHeight;

            int buttonWidth = (int) (rawW * buttonScale);
            int buttonHeight = (int) (rawH * buttonScale);

            int x = centerX - (buttonWidth / 2) + buttonOffsetX;
            int y = startY + i * (baseButtonHeight + gap);

            // Draw button
            if (btn != null) {
                g2.drawImage(btn, x, y, buttonWidth, buttonHeight, null);
            } else {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }

            // Highlight
            if (i == selectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }
        }
    }

    // ========================== INPUT ==========================
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + 2) % 2;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % 2;
    }

    public void select() {
        if (selectedIndex == 0) gp.startNewGame();              // Retry
        if (selectedIndex == 1) gp.gameState = GamePanel.STATE_MENU; // Quit
    }
}
