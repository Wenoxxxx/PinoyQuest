package src.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;

public class InventoryUI {

    private final GamePanel gp;
    private final Player player;

    private BufferedImage inventoryPanel;

    // === PRECISE RESIZABLE PANEL ===
    private int panelWidth  = 490;  
    private int panelHeight = 240;  

    public InventoryUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        try {
            File file = new File("src/assets/ui/inventory/inventory_panel.png");
            inventoryPanel = ImageIO.read(file);
            System.out.println("InventoryPanel Loaded: " + inventoryPanel);
        } catch (Exception e) {
            System.out.println("ERROR loading inventory panel!");
            e.printStackTrace();
        }
    }

    private int selectedRow = 0;
    private int selectedCol = 0;

    private final int maxRows = 2;
    private final int maxCols = 3;

    public void moveCursorUp() { if (selectedRow > 0) selectedRow--; }
    public void moveCursorDown() { if (selectedRow < maxRows - 1) selectedRow++; }
    public void moveCursorLeft() { if (selectedCol > 0) selectedCol--; }
    public void moveCursorRight() { if (selectedCol < maxCols - 1) selectedCol++; }

    public void useSelectedItem() {
        Item item = player.inventory[selectedRow][selectedCol];
        if (item == null) return;

        item.use(player);
        player.inventory[selectedRow][selectedCol] = null;
    }

   public void draw(Graphics2D g2) {

        int invX = 0;
        int invY = 145;

        if (inventoryPanel == null) {
            g2.setColor(Color.RED);
            g2.drawString("Failed to load inventory panel!", invX, invY);
            return;
        }

        // Draw panel
        g2.drawImage(inventoryPanel, invX, invY, panelWidth, panelHeight, null);

        // Slot grid settings
        int cols = 3;
        int rows = 2;
        int slotSize = 64;
        int slotPadding = 10;


        int slotOffsetX = 76;  
        int slotOffsetY = 80;   

        int startX = invX + slotOffsetX;
        int startY = invY + slotOffsetY;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                int x = startX + col * (slotSize + slotPadding);
                int y = startY + row * (slotSize + slotPadding);

                g2.setColor(new Color(60, 60, 60, 180));
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);

                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

                if (row == selectedRow && col == selectedCol) {
                    g2.setColor(new Color(255, 255, 0, 120));
                    g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
                }
            }
        }
    }

}
