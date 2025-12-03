package src.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;
import src.items.weapons.Hanger;
import src.items.weapons.Tsinelas;

public class InventoryUI {

    private final GamePanel gp;
    private final Player player;

    private BufferedImage inventoryPanel;

    private int panelWidth  = 490;
    private int panelHeight = 240;

    private int selectedRow = 0;
    private int selectedCol = 0;

    private final int maxRows = 2;
    private final int maxCols = 3;

    // ====== HOLD SYSTEM ======
    private boolean holdingItem = false;
    private Item heldItem = null;

    // ORIGINAL slot where the held item came from
    private int originRow = -1;
    private int originCol = -1;

    public InventoryUI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;

        try {
            inventoryPanel = ImageIO.read(
                new File("src/assets/ui/inventory/inventory_panel.png")
            );
        } catch (Exception e) {
            System.out.println("ERROR loading inventory panel!");
        }
    }

    public boolean isHoldingItem() {
        return holdingItem;
    }

    // ===========================
    //   USE ITEM
    // ===========================
    public void useSelectedItem() {

        Item item = player.inventory[selectedRow][selectedCol];
        if (item == null) {
            player.resetToDefaultAnimation();
            return;
        }

        // Check if it's a weapon (Hanger or Tsinelas)
        if (item instanceof Hanger || item instanceof Tsinelas) {
            player.equipWeaponItem(item);
            if (selectedRow == 0) player.syncHotbarFromInventory();
            return;
        }

        item.use(player);

        if (item.isConsumable) {
            player.inventory[selectedRow][selectedCol] = null;
            if (selectedRow == 0) player.syncHotbarFromInventory();
        }

        gp.ui.showMessage(item.name + " used!");
    }

    // ===========================
    //   BEGIN HOLD (SHIFT down)
    // ===========================
    public void beginHoldSelectedItem() {

        if (holdingItem) return;

        Item slot = player.inventory[selectedRow][selectedCol];
        if (slot == null) return;

        originRow = selectedRow;
        originCol = selectedCol;

        heldItem = slot;
        holdingItem = true;

        // temporarily remove from original position
        player.inventory[originRow][originCol] = null;
    }

    // ===========================
    //   FINISH SWAP (SHIFT release)
    // ===========================
    public void finishHoldSwap() {

        if (!holdingItem || heldItem == null) return;

        // If dropped back to origin → restore
        if (selectedRow == originRow && selectedCol == originCol) {
            player.inventory[originRow][originCol] = heldItem;
            clearHold();
            if (originRow == 0) player.syncHotbarFromInventory();
            return;
        }

        Item target = player.inventory[selectedRow][selectedCol];

        if (target == null) {
            // Empty → place held item here
            player.inventory[selectedRow][selectedCol] = heldItem;
        } else {
            // Swap:
            player.inventory[selectedRow][selectedCol] = heldItem;
            player.inventory[originRow][originCol] = target;
        }

        clearHold();

        if (selectedRow == 0 || originRow == 0) {
            player.syncHotbarFromInventory();
        }
    }

    private void clearHold() {
        holdingItem = false;
        heldItem = null;
        originRow = -1;
        originCol = -1;
    }

    // ===========================
    //   CURSOR MOVEMENT
    // ===========================
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

    // ===========================
    //   DRAW
    // ===========================

    public int invX = 750;   // change to move left/right
    public int invY = 400;   // change to move up/down

    public void draw(Graphics2D g2) {

        int drawX = invX;
        int drawY = invY;

        // Draw panel
        if (inventoryPanel != null)
            g2.drawImage(inventoryPanel, drawX, drawY, panelWidth, panelHeight, null);

        int slotSize = 64;
        int slotPadding = 10;

        int startX = drawX + 76;
        int startY = drawY + 80;

        for (int row = 0; row < maxRows; row++) {
            for (int col = 0; col < maxCols; col++) {

                int x = startX + col * (slotSize + slotPadding);
                int y = startY + row * (slotSize + slotPadding);

                g2.setColor(new Color(60, 60, 60, 180));
                g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);

                Item slot = player.inventory[row][col];
                if (slot != null && slot.sprite != null) {
                    g2.drawImage(slot.sprite, x + 6, y + 6, slotSize - 12, slotSize - 12, null);
                }

                g2.setColor(Color.WHITE);
                g2.drawRoundRect(x, y, slotSize, slotSize, 10, 10);

                if (row == selectedRow && col == selectedCol) {
                    g2.setColor(new Color(255, 255, 0, 120));
                    g2.fillRoundRect(x, y, slotSize, slotSize, 10, 10);
                }
            }
        }

        // Show held item
        if (holdingItem && heldItem != null && heldItem.sprite != null) {

            int cursorX = startX + selectedCol * (slotSize + slotPadding);
            int cursorY = startY + selectedRow * (slotSize + slotPadding);

            g2.drawImage(
                heldItem.sprite,
                cursorX + 6,
                cursorY + 6,
                slotSize - 12,
                slotSize - 12,
                null
            );

            g2.setColor(Color.WHITE);
            g2.drawRect(cursorX + 6, cursorY + 6, slotSize - 12, slotSize - 12);
        }
    }


}
