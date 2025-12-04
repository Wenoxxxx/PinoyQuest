package src.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import src.core.GamePanel;

public class VictoryUI {

    private final GamePanel gp;

    private BufferedImage popupBackground;
    private BufferedImage continueButton;

    private final double scale = 5.0;

    public VictoryUI(GamePanel gp) {
        this.gp = gp;
        loadSprites();
    }

    private void loadSprites() {
        try {
            popupBackground = ImageIO.read(new File("src/assets/ui/victory/victoryBg.png"));
            continueButton  = ImageIO.read(new File("src/assets/ui/victory/victoryContinue.png"));
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

        drawPopup(g2, screenW, screenH);
        drawContinueButton(g2, screenW, screenH);
    }

    private void drawPopup(Graphics2D g2, int screenW, int screenH) {
        if (popupBackground != null) {
            int rawW = popupBackground.getWidth();
            int rawH = popupBackground.getHeight();

            int drawW = (int)(rawW * scale);
            int drawH = (int)(rawH * scale);

            int x = (screenW - drawW) / 2;
            int y = (screenH - drawH) / 2 - 200;

            g2.drawImage(popupBackground, x, y, drawW, drawH, null);
        }
    }

    private void drawContinueButton(Graphics2D g2, int screenW, int screenH) {
        if (continueButton == null) return;

        int rawW = continueButton.getWidth();
        int rawH = continueButton.getHeight();

        int drawW = (int)(rawW * scale);
        int drawH = (int)(rawH * scale);

        int x = (screenW - drawW) / 2;
        int y = screenH / 2 + 50;

        g2.drawImage(continueButton, x, y, drawW, drawH, null);

        // Optional: glow effect
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, drawW, drawH, 20, 20);
    }
}
