package src.items;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import src.core.GamePanel;
import src.items.consumables.HealthRegenItem;
import src.items.consumables.NoCooldownItem;
import src.items.consumables.ShieldItem;
import src.items.temporary.Map2Key;
import src.items.weapons.Hanger;

public class ItemManager {

    private final GamePanel gp;

    // per-map items
    public final ArrayList<Item>[] items = new ArrayList[3];

    // Item template types
    private final Item[] itemTypes = new Item[32];
    private int itemTypeCount = 0;

    private static final String MAP_DIR =
            "src" + File.separator + "assets" + File.separator + "maps" + File.separator;

    public ItemManager(GamePanel gp) {
        this.gp = gp;

        for (int i = 0; i < items.length; i++)
            items[i] = new ArrayList<>();

        registerItemTypes();

        loadItemMap("items1.txt", 0);
        loadItemMap("items2.txt", 1);
    }

    // ============================================================
    // REGISTER ITEM TYPES
    // ============================================================
    private void registerItemTypes() {
        addType(new HealthRegenItem(gp, 0, 0));
        addType(new NoCooldownItem(gp, 0, 0));
        addType(new ShieldItem(gp, 0, 0));
        addType(new Map2Key(gp, 0, 0));
        addType(new Hanger(gp, 0, 0)); // ID 4

        System.out.println("Registered " + itemTypeCount + " item types.");
    }

    private void addType(Item item) {
        itemTypes[itemTypeCount++] = item;
    }

    // ============================================================
    // LOAD ITEM MAP
    // ============================================================
    private void loadItemMap(String fileName, int mapIndex) {

        File file = new File(MAP_DIR + fileName);
        if (!file.exists()) {
            System.out.println("Item map not found: " + fileName);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                String[] values = line.trim().split("\\s+");

                for (int col = 0; col < values.length && col < gp.maxWorldCol; col++) {

                    int id = Integer.parseInt(values[col]);
                    if (id < 0 || id >= itemTypeCount) continue;

                    int worldX = col * gp.tileSize;
                    int worldY = row * gp.tileSize;

                    Item template = itemTypes[id];
                    Item newItem = template.copy(gp, worldX, worldY);

                    if (newItem != null && newItem.sprite != null) {
                        int spriteW = newItem.sprite.getWidth();
                        int spriteH = newItem.sprite.getHeight();

                        newItem.worldX = worldX + (gp.tileSize - spriteW) / 2;
                        newItem.worldY = worldY + (gp.tileSize - spriteH) / 2;
                    }

                    if (newItem != null)
                        items[mapIndex].add(newItem);
                }
                row++;
            }

        } catch (Exception e) {
            System.out.println("Error loading item map: " + fileName);
            e.printStackTrace();
        }
    }

    // ============================================================
    // UPDATE (PICKUP LOGIC)
    // ============================================================
    public void update() {

        ArrayList<Item> mapItems = items[gp.currentMap];
        if (mapItems == null) return;

        Rectangle playerHit = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        for (Item item : mapItems) {

            if (item == null || item.consumed) continue;

            int boxW = item.pickupWidth > 0 ? item.pickupWidth : gp.tileSize;
            int boxH = item.pickupHeight > 0 ? item.pickupHeight : gp.tileSize;

            Rectangle itemBox = new Rectangle(
                    item.worldX + item.pickupOffsetX,
                    item.worldY + item.pickupOffsetY,
                    boxW,
                    boxH
            );

            if (playerHit.intersects(itemBox)) {

                // ❗ HOTFIX: Prevent pickup while dragging (SHIFT)
                if (gp.ui.getInventoryUI().isHoldingItem()) {
                    gp.ui.showMessage("Finish swapping first!");
                    continue;
                }

                boolean stored = gp.player.addToInventory(item);

                if (stored) {
                    item.onPickup();
                    item.consumed = true;
                    gp.ui.showMessage("Picked up: " + item.name);
                } else {
                    gp.ui.showMessage("Inventory full!");
                }
            }
        }

        mapItems.removeIf(i -> i.consumed);
    }

    // ============================================================
    // DRAW BEHIND PLAYER
    // ============================================================
    public void drawBehindPlayer(Graphics2D g2, int playerFeetRow) {

        ArrayList<Item> mapItems = items[gp.currentMap];
        if (mapItems == null) return;

        for (Item item : mapItems) {
            if (item == null || item.sprite == null) continue;

            int bottom = item.worldY + gp.tileSize;
            int row = (bottom - 1) / gp.tileSize;

            if (row < playerFeetRow)
                item.draw(g2);
        }
    }

    // ============================================================
    // DRAW IN FRONT OF PLAYER
    // ============================================================
    public void drawInFrontOfPlayer(Graphics2D g2, int playerFeetRow) {

        ArrayList<Item> mapItems = items[gp.currentMap];
        if (mapItems == null) return;

        for (Item item : mapItems) {
            if (item == null || item.sprite == null) continue;

            int bottom = item.worldY + gp.tileSize;
            int row = (bottom - 1) / gp.tileSize;

            if (row >= playerFeetRow)
                item.draw(g2);
        }
    }
}
