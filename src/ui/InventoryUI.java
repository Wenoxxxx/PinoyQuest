package src.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;


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

    public void useSelectedItem() {

        Item item = player.inventory[selectedRow][selectedCol];

        if (item == null) {
            System.out.println("No item in this slot.");
            return;
        }

        // Use item
        item.use(player);

        // Remove after use
        player.inventory[selectedRow][selectedCol] = null;

        System.out.println("Item used!");
    }

    public void draw(Graphics2D g2) {

        // Popup background
        int invX = 10;
        int invY = 145;
        int invWidth = 320;
        int invHeight = 200;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(invX, invY, invWidth, invHeight, 18, 18);

        g2.setColor(Color.WHITE);
        g2.drawString("Inventory", invX + 20, invY + 30);

        int cols = 3;
        int rows = 2;
        int slotSize = 64;
        int slotPadding = 10;

        int gridWidth = (slotSize * cols) + (slotPadding * (cols - 1));
        int gridHeight = (slotSize * rows) + (slotPadding * (rows - 1));

        int startX = invX + (invWidth - gridWidth) / 2;
        int startY = invY + (invHeight - gridHeight) / 2 + 20;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int x = startX + col * (slotSize + slotPadding);
                int y = startY + row * (slotSize + slotPadding);

                g2.setColor(new Color(60, 60, 60, 180));
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);

                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

                if (row == selectedRow && col == selectedCol) {
                    g2.setColor(new Color(255, 255, 0, 160));
                    g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
                }
            }
        }
    }
}
