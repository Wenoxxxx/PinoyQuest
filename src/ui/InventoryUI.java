package src.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;

public class InventoryUI {

    private final GamePanel gp;
    private final Player player;

    public InventoryUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
    }



    private int selectedRow = 0;
    private int selectedCol = 0;
    private final int maxRows = 2;
    private final int maxCols = 3;

    
    public void moveCursorUp() {
        if (selectedRow > 0) selectedRow--;
    }

    public void moveCursorDown() {
        if (selectedRow < maxRows - 1) selectedRow++;
    }

    public void moveCursorLeft() {
        if (selectedCol > 0) selectedCol--;
    }

    public void moveCursorRight() {
        if (selectedCol < maxCols - 1) selectedCol++;
    }


    public void draw(Graphics2D g2) {

        // === Popup Background ===
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(100, 100, 400, 300, 18, 18);

        // Text
        g2.setColor(Color.WHITE);
        g2.drawString("Inventory", 120, 130);

        // === GRID SETTINGS ===
        int cols = 3;
        int rows = 2;

        int slotSize = 64;       // size of each slot
        int slotPadding = 10;    // space between slots

        int gridWidth = (slotSize * cols) + (slotPadding * (cols - 1));
        int gridHeight = (slotSize * rows) + (slotPadding * (rows - 1));

        // Center grid inside the popup
        int startX = 100 + (400 - gridWidth) / 2;
        int startY = 100 + (300 - gridHeight) / 2 + 20; // +20 to move grid lower

        // === DRAW GRID SLOTS ===
        g2.setColor(new Color(180, 180, 180, 180)); // slot background
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int x = startX + col * (slotSize + slotPadding);
                int y = startY + row * (slotSize + slotPadding);

                // slot background
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);

                // slot border
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

                // Highlight current selected slot
                if (row == selectedRow && col == selectedCol) {
                    g2.setColor(new Color(255, 255, 0, 160)); // yellow highlight
                    g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
                }


                g2.setColor(new Color(180, 180, 180, 180)); // reset color
            }
        }
    }
}
