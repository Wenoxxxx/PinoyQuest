package src.items;

import java.awt.*;
import java.util.ArrayList;
import src.core.GamePanel;

public class ItemManager {

    private GamePanel gp;

    public final ArrayList<Item> items = new ArrayList<>();

    public ItemManager(GamePanel gp) {
        this.gp = gp;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void update() {

        Rectangle playerHit = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height);

        for (Item item : items) {
            if (item == null || item.consumed)
                continue;

            Rectangle itemBox = new Rectangle(
                    item.worldX,
                    item.worldY,
                    gp.tileSize * item.widthTiles,
                    gp.tileSize * item.heightTiles);

            if (playerHit.intersects(itemBox)) {
                item.onPickup();
                item.consumed = true;
            }
        }

        items.removeIf(i -> i.consumed);
    }

    public void drawBehindPlayer(Graphics2D g2, int playerFeetRow) {

        for (Item item : items) {
            if (item == null || item.sprite == null)
                continue;

            int itemBottom = item.worldY + gp.tileSize * item.heightTiles;
            int itemRow = (itemBottom - 1) / gp.tileSize;

            if (itemRow < playerFeetRow) {
                item.draw(g2);
            }
        }
    }

    public void drawInFrontOfPlayer(Graphics2D g2, int playerFeetRow) {

        for (Item item : items) {
            if (item == null || item.sprite == null)
                continue;

            int itemBottom = item.worldY + gp.tileSize * item.heightTiles;
            int itemRow = (itemBottom - 1) / gp.tileSize;

            if (itemRow >= playerFeetRow) {
                item.draw(g2);
            }
        }
    }
}
