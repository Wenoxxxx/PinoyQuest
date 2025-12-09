package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import src.core.GamePanel;

public class VictoryUI {

    private final GamePanel gp;

    private BufferedImage popupBackground;
    private BufferedImage quitButtonSprite;

    // Button settings
    public double quitButtonScale = 5.5;

    // Manual button position - adjust these to position button where you want
    public int quitButtonX = 490; // Manual X position for quit menu button
    public int quitButtonY = 50; // Manual Y position for quit menu button

    // Highlight settings
    public int highlightStroke = 3;

    public VictoryUI(GamePanel gp) {
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Try to load victory background if it exists
            File bgFile = new File("assets/ui/victory/victoryBg.png");
            if (bgFile.exists()) {
                popupBackground = ImageIO.read(bgFile);
            }

            // Load quit menu button (same as game over)
            quitButtonSprite = ImageIO.read(new File("assets/ui/gameover/gameOverQuitBtn.png"));
        } catch (Exception e) {
            System.out.println("[VictoryUI] Failed to load assets!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        // Dim background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenW, screenH);

        // Draw huge VICTORY text
        drawVictoryText(g2, screenW, screenH);

        // Draw quit menu button
        drawButton(g2);
    }

    private void drawVictoryText(Graphics2D g2, int screenW, int screenH) {
        // Huge VICTORY text
        g2.setFont(new Font("Arial", Font.BOLD, 120));
        g2.setColor(Color.YELLOW);

        String victoryText = "VICTORY!";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(victoryText);
        int textX = (screenW - textWidth) / 2;
        int textY = screenH / 3; // Position in upper third of screen

        // Draw text with shadow for better visibility
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(victoryText, textX + 4, textY + 4);
        g2.setColor(Color.YELLOW);
        g2.drawString(victoryText, textX, textY);
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

    private void drawButton(Graphics2D g2) {
        if (quitButtonSprite == null) {
            // Draw fallback button if image failed to load
            int buttonWidth = 260;
            int buttonHeight = 60;
            int x = quitButtonX;
            int y = quitButtonY;

            g2.setColor(new Color(100, 100, 100, 200));
            g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 26));
            g2.drawString("QUIT MENU", x + 10, y + 40);

            // Highlight fallback button
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(highlightStroke));
            g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            return;
        }

        int rawW = quitButtonSprite.getWidth();
        int rawH = quitButtonSprite.getHeight();
        int buttonWidth = (int) (rawW * quitButtonScale);
        int buttonHeight = (int) (rawH * quitButtonScale);

        // Use manual position
        int x = quitButtonX;
        int y = quitButtonY;

        g2.drawImage(quitButtonSprite, x, y, buttonWidth, buttonHeight, null);

        // Highlight button - wrap tightly around visible button image (same as
        // GameOverUI)
        Rectangle visibleBounds = getVisibleBounds(quitButtonSprite);

        if (visibleBounds.width > 0 && visibleBounds.height > 0) {
            // Calculate scaled visible bounds
            int visibleX = x + (int) (visibleBounds.x * quitButtonScale);
            int visibleY = y + (int) (visibleBounds.y * quitButtonScale);
            int visibleW = (int) (visibleBounds.width * quitButtonScale);
            int visibleH = (int) (visibleBounds.height * quitButtonScale);

            // Draw highlight tightly around visible button area
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(highlightStroke));
            g2.drawRoundRect(visibleX, visibleY, visibleW, visibleH, 20, 20);
        } else {
            // Fallback: draw around full button if no visible bounds found
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(highlightStroke));
            g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
        }
    }

    // Inputs (no navigation needed since there's only one button)
    public void moveUp() {
        // No-op, only one button
    }

    public void moveDown() {
        // No-op, only one button
    }

    public void select() {
        // Go to main menu and restore main menu music
        gp.stopMusic();
        gp.playMusic(2); // Main menu music (start.wav)
        gp.gameState = GamePanel.STATE_MENU;
    }

    // Click handler - detect mouse clicks on button
    public void handleMouseClick(int mouseX, int mouseY) {
        if (quitButtonSprite == null) {
            // Check fallback button
            int buttonWidth = 260;
            int buttonHeight = 60;
            int x = quitButtonX;
            int y = quitButtonY;

            if (mouseX >= x && mouseX <= x + buttonWidth &&
                    mouseY >= y && mouseY <= y + buttonHeight) {
                select();
            }
            return;
        }

        int rawW = quitButtonSprite.getWidth();
        int rawH = quitButtonSprite.getHeight();
        int buttonWidth = (int) (rawW * quitButtonScale);
        int buttonHeight = (int) (rawH * quitButtonScale);

        // Use manual position (same as drawButton)
        int x = quitButtonX;
        int y = quitButtonY;

        if (mouseX >= x && mouseX <= x + buttonWidth &&
                mouseY >= y && mouseY <= y + buttonHeight) {
            select();
        }
    }

    public void update() {
    }
}
