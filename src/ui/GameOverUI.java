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

    // GLOBAL button gap (optional)
    public int buttonGap = 35;

    // INDIVIDUAL BUTTON SCALES
    public double retryButtonScale = 4.0;
    public double quitButtonScale = 4.0;

    // INDIVIDUAL BUTTON POSITIONS
    public int retryButtonOffsetX = 0;
    public int retryButtonOffsetY = -150;

    public int quitButtonOffsetX = 0;
    public int quitButtonOffsetY = -10;

    // INDIVIDUAL HIGHLIGHT POSITIONS (INDEPENDENT)
    public int retryHighlightX = 565; // Set absolute screen X
    public int retryHighlightY = 340; // Set absolute screen Y

    public int quitHighlightX = 5; // Set absolute screen X
    public int quitHighlightY = 340; // Set absolute screen Y

    // Highlight box settings
    public int highlightPaddingX = 6;
    public int highlightPaddingY = 6;
    public int highlightStroke = 4;

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
            popupBackground = ImageIO.read(new File("src/assets/ui/gameover/gameOverBg.png"));
            buttonSprites[0] = ImageIO.read(new File("src/assets/ui/gameover/gameOverBtn.png"));
            buttonSprites[1] = ImageIO.read(new File("src/assets/ui/gameover/gameOverQuitBtn.png"));
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

    private void drawButtons(Graphics2D g2) {

        // ---------------- RETRY BUTTON ----------------
        BufferedImage retry = buttonSprites[0];
        if (retry != null) {
            int rw = (int) (retry.getWidth() * retryButtonScale);
            int rh = (int) (retry.getHeight() * retryButtonScale);

            int rx = popupDrawX + (popupDrawW - rw) / 2 + retryButtonOffsetX;
            int ry = popupDrawY + popupDrawH + retryButtonOffsetY;

            g2.drawImage(retry, rx, ry, rw, rh, null);

            if (selectedIndex == 0) {
                drawHighlight(g2, retryHighlightX, retryHighlightY, rw, rh);
            }
        }

        // ---------------- QUIT BUTTON ----------------
        BufferedImage quit = buttonSprites[1];
        if (quit != null) {
            int qw = (int) (quit.getWidth() * quitButtonScale);
            int qh = (int) (quit.getHeight() * quitButtonScale);

            int qx = popupDrawX + (popupDrawW - qw) / 2 + quitButtonOffsetX;
            int qy = popupDrawY + popupDrawH + quitButtonOffsetY + buttonGap;

            g2.drawImage(quit, qx, qy, qw, qh, null);

            if (selectedIndex == 1) {
                drawHighlight(g2, quitHighlightX, quitHighlightY, qw, qh);
            }
        }
    }

    private void drawHighlight(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(highlightStroke));

        g2.drawRoundRect(
                x - highlightPaddingX,
                y - highlightPaddingY,
                w + highlightPaddingX * 2,
                h + highlightPaddingY * 2,
                25, 25);
    }

    // Inputs
    public void moveUp() {
        selectedIndex = (selectedIndex - 1 + 2) % 2;
    }

    public void moveDown() {
        selectedIndex = (selectedIndex + 1) % 2;
    }

    public void select() {
        if (selectedIndex == 0)
            gp.startNewGame();
        else
            gp.gameState = GamePanel.STATE_MENU;
    }

    public void update() {
    }
}
