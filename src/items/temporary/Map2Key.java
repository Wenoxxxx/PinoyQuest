package src.items.temporary;

import src.core.GamePanel;
import src.items.Item;
public class Map2Key extends Item {

    public Map2Key(GamePanel gp, int x, int y) {
        super(gp, x, y, "Map2 Key", "Unlocks the path to Map 2.");
    }

    @Override
    public void onPickup() {
        gp.player.hasMap2Key = true;
        this.consumed = true;
    }
}
