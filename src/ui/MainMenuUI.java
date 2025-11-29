package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;

public class MainMenuUI {

    private final GamePanel gp;

    private BufferedImage menuBackground;
    private BufferedImage[] buttonSprites = new BufferedImage[4];

    public MainMenuUI(GamePanel gp) {
        this.gp = gp;
        loadMenuSprites();
    }

    // ========================== LOAD ASSETS ==========================
    private void loadMenuSprites() {
        try {
            menuBackground = ImageIO.read(new File("src/assets/ui/mainMenu/BG.png"));

            buttonSprites[0] = ImageIO.read(new File("src/assets/ui/mainMenu/btn1.png")); // Start
            buttonSprites[1] = ImageIO.read(new File("src/assets/ui/mainMenu/btn2.png")); // Resume
            buttonSprites[2] = ImageIO.read(new File("src/assets/ui/mainMenu/btn3.png")); // Settings
            buttonSprites[3] = ImageIO.read(new File("src/assets/ui/mainMenu/btn4.png")); // Quit

        } catch (Exception e) {
            System.out.println("[MainMenuUI] Failed loading menu sprites!");
            e.printStackTrace();
        }
    }

    // ========================== MAIN DRAW ==========================
    public void draw(Graphics2D g2) {

        int screenW = gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
        int screenH = gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;

        drawBackground(g2, screenW, screenH);
        drawButtons(g2, screenW, screenH);
        drawHint(g2, screenW, screenH);
    }

    // ========================== BACKGROUND ==========================
    private void drawBackground(Graphics2D g2, int screenW, int screenH) {

        if (menuBackground != null) {
            int bgW = menuBackground.getWidth();
            int bgH = menuBackground.getHeight();

            double scaleX = (double) screenW / bgW;
            double scaleY = (double) screenH / bgH;
            double scale = Math.min(scaleX, scaleY);

            int drawW = (int) (bgW * scale);
            int drawH = (int) (bgH * scale);

            int x = (screenW - drawW) / 2;
            int y = (screenH - drawH) / 2;

            g2.drawImage(menuBackground, x, y, drawW, drawH, null);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenW, screenH);
        }
    }

    // ========================== BUTTONS ==========================
    private void drawButtons(Graphics2D g2, int screenW, int screenH) {

        int defaultButtonWidth = 260;
        int defaultButtonHeight = 60;

        double buttonScale = 5.5;
        int centerX = screenW / 2;
        int gap = 5;

        // Get correct button height
        int baseButtonHeight = defaultButtonHeight;
        if (buttonSprites[0] != null) {
            baseButtonHeight = (int) (buttonSprites[0].getHeight() * buttonScale);
        }

        // Starting Y so menu is centered
        int startY = screenH / 2 - 2 * (baseButtonHeight + gap) + 60;

        g2.setFont(new Font("Arial", Font.PLAIN, 26));

        for (int i = 0; i < gp.menuOptions.length; i++) {

            BufferedImage btnImg = buttonSprites[i];

            int rawW = (btnImg != null) ? btnImg.getWidth() : defaultButtonWidth;
            int rawH = (btnImg != null) ? btnImg.getHeight() : defaultButtonHeight;

            int buttonWidth = (int) (rawW * buttonScale);
            int buttonHeight = (int) (rawH * buttonScale);

            int x = centerX - (buttonWidth / 2);
            int y = startY + i * (baseButtonHeight + gap);

            // Draw button image or fallback
            if (btnImg != null) {
                g2.drawImage(btnImg, x, y, buttonWidth, buttonHeight, null);
            } else {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }

            // Highlight selected
            if (i == gp.menuSelectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 20, 20);
            }
        }
    }

    // ========================== HINT TEXT ==========================
    private void drawHint(Graphics2D g2, int screenW, int screenH) {
        g2.setFont(new Font("Arial", Font.ITALIC, 18));

        String hint = "Use W/S or ↑/↓ to move, Enter to select";
        int w = g2.getFontMetrics().stringWidth(hint);

        g2.setColor(Color.WHITE);
        g2.drawString(hint, (screenW - w) / 2, screenH - 40);
    }
}
