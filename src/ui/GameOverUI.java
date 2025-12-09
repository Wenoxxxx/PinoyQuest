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

    // Popup positioning
    public double popupScale = 4.0;
    public int popupOffsetX = 0;
    public int popupOffsetY = -250;

    // Manual button positions - adjust these to position buttons where you want
    public int retryButtonX = 490; // Manual X position for retry button (gameOverBtn.png)
    public int retryButtonY = 50; // Manual Y position for retry button
    public int quitButtonX = 490; // Manual X position for quit button (gameOverQuitBtn.png)
    public int quitButtonY = 300; // Manual Y position for quit button

    // Selected button index
    private int selectedIndex = 0;

    // Cached popup draw area
    private int popupDrawX, popupDrawY, popupDrawW, popupDrawH;

    public GameOverUI(GamePanel gp) {
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        try {
            popupBackground = ImageIO.read(new File("assets/ui/gameover/gameOverBg.png"));
            buttonSprites[0] = ImageIO.read(new File("assets/ui/gameover/gameOverBtn.png"));
            buttonSprites[1] = ImageIO.read(new File("assets/ui/gameover/gameOverQuitBtn.png"));

            // Debug: check if buttons loaded
            if (buttonSprites[0] == null) {
                System.out.println("[GameOverUI] WARNING: gameOverBtn.png failed to load!");
            }
            if (buttonSprites[1] == null) {
                System.out.println("[GameOverUI] WARNING: gameOverQuitBtn.png failed to load!");
            }
        } catch (Exception e) {
            System.out.println("[GameOverUI] Failed to load assets!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // Dim background
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, screenW, screenH);

        drawPopup(g2, screenW, screenH);
        drawButtons(g2);
    }

    private void drawPopup(Graphics2D g2, int screenW, int screenH) {
        if (popupBackground != null) {
            int rawW = popupBackground.getWidth();
            int rawH = popupBackground.getHeight();

            popupDrawW = (int) (rawW * popupScale);
            popupDrawH = (int) (rawH * popupScale);

            popupDrawX = (screenW - popupDrawW) / 2 + popupOffsetX;
            popupDrawY = (screenH - popupDrawH) / 2 + popupOffsetY;

            g2.drawImage(popupBackground, popupDrawX, popupDrawY, popupDrawW, popupDrawH, null);
        }
    }

    // Helper method to get visible (non-transparent) bounds of an image
    private Rectangle getVisibleBounds(BufferedImage img) {
        if (img == null)
            return new Rectangle(0, 0, 0, 0);

        int minX = img.getWidth();
        int minY = img.getHeight();
        int maxX = 0;
        int maxY = 0;

        boolean hasVisiblePixel = false;

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = img.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                if (alpha > 0) { // Non-transparent pixel
                    hasVisiblePixel = true;
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }

        if (!hasVisiblePixel) {
            return new Rectangle(0, 0, img.getWidth(), img.getHeight());
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private void drawButtons(Graphics2D g2) {
        // Use same button scale as main menu
        double buttonScale = 5.5;

        // Get button height
        int defaultButtonHeight = 60;

        for (int i = 0; i < 2; i++) {
            BufferedImage btnImg = buttonSprites[i];

            int rawW = (btnImg != null) ? btnImg.getWidth() : 260;
            int rawH = (btnImg != null) ? btnImg.getHeight() : defaultButtonHeight;

            int buttonWidth = (int) (rawW * buttonScale);
            int buttonHeight = (int) (rawH * buttonScale);

            // Use manual positions for each button
            int x, y;
            if (i == 0) {
                // Retry button (gameOverBtn.png)
                x = retryButtonX;
                y = retryButtonY;
            } else {
                // Quit button (gameOverQuitBtn.png)
                x = quitButtonX;
                y = quitButtonY;
            }

            // Draw button image or fallback
            if (btnImg != null) {
                g2.drawImage(btnImg, x, y, buttonWidth, buttonHeight, null);

                // Highlight selected button - wrap tightly around visible button image
                if (i == selectedIndex) {
                    // Get visible bounds of the button image (ignoring transparent padding)
                    Rectangle visibleBounds = getVisibleBounds(btnImg);

                    if (visibleBounds.width > 0 && visibleBounds.height > 0) {
                        // Calculate scaled visible bounds
                        int visibleX = x + (int) (visibleBounds.x * buttonScale);
                        int visibleY = y + (int) (visibleBounds.y * buttonScale);
                        int visibleW = (int) (visibleBounds.width * buttonScale);
                        int visibleH = (int) (visibleBounds.height * buttonScale);

                        // Draw highlight tightly around visible button area
                        g2.setColor(Color.YELLOW);
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(visibleX, visibleY, visibleW, visibleH, 20, 20);
                    } else {
                        // Fallback: draw around full button if no visible bounds found
                        g2.setColor(Color.YELLOW);
                        g2.setStroke(new BasicStroke(3));
                        g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
                    }
                }
            } else {
                // Draw fallback rectangle if image failed to load
                g2.setColor(new Color(100, 100, 100, 200));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.PLAIN, 26));
                g2.drawString(i == 0 ? "RESTART" : "QUIT", x + 10, y + 40);

                // Highlight fallback button
                if (i == selectedIndex) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
                }
            }
        }
    }

    // Inputs
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + 2) % 2;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % 2;
    }

    public void select() {
        if (selectedIndex == 0) {
            // Retry: restore full health and energy, face down, restart
            gp.player.heal(gp.player.getMaxHealth());
            gp.player.restoreEnergy(gp.player.getMaxEnergy());
            gp.player.direction = "down";
            gp.keyHandler.resetMovementKeys();
            gp.switchToMap(0, 15, 10, "down");
            gp.gameState = GamePanel.STATE_PLAY;
        } else {
            // Quit: go to main menu
            gp.gameState = GamePanel.STATE_MENU;
        }
    }

    // Click handler - detect mouse clicks on buttons
    public void handleMouseClick(int mouseX, int mouseY) {
        double buttonScale = 5.5;

        for (int i = 0; i < 2; i++) {
            BufferedImage btnImg = buttonSprites[i];
            if (btnImg == null)
                continue;

            int rawW = btnImg.getWidth();
            int rawH = btnImg.getHeight();
            int buttonWidth = (int) (rawW * buttonScale);
            int buttonHeight = (int) (rawH * buttonScale);

            // Use manual positions for each button (same as drawButtons)
            int x, y;
            if (i == 0) {
                // Retry button (gameOverBtn.png)
                x = retryButtonX;
                y = retryButtonY;
            } else {
                // Quit button (gameOverQuitBtn.png)
                x = quitButtonX;
                y = quitButtonY;
            }

            if (mouseX >= x && mouseX <= x + buttonWidth &&
                    mouseY >= y && mouseY <= y + buttonHeight) {
                selectedIndex = i;
                select();
                break;
            }
        }
    }

    public void update() {
    }
}
