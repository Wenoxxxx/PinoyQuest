package src.core;

import java.awt.Rectangle;
import src.entity.Entity;

public class Collision {

    private final GamePanel gp;

    public Collision(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Checks whether the given entity would collide with either:
     *  - a blocking tile, or
     *  - a blocking object (house, tree, etc.)
     *
     * at the next world position (nextWorldX, nextWorldY).
     */
    public boolean willCollide(Entity entity, int nextWorldX, int nextWorldY) {
        if (entity == null) {
            return false;
        }

        Rectangle area = entity.solidArea;
        if (area == null || area.width <= 0 || area.height <= 0) {
            return false;
        }

        // ===== TILE COLLISION =====
        int left   = nextWorldX + area.x;
        int right  = nextWorldX + area.x + area.width  - 1;
        int top    = nextWorldY + area.y;
        int bottom = nextWorldY + area.y + area.height - 1;

        int leftCol   = Math.floorDiv(left,   gp.tileSize);
        int rightCol  = Math.floorDiv(right,  gp.tileSize);
        int topRow    = Math.floorDiv(top,    gp.tileSize);
        int bottomRow = Math.floorDiv(bottom, gp.tileSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (gp.isTileBlocked(col, row)) {
                    return true;
                }
            }
        }

        // ===== OBJECT COLLISION =====
        // Uses improved hitboxes in ObjectManager (based on sprite size, not tiles)
        if (gp.isObjectBlocked(nextWorldX, nextWorldY, area)) {
            return true;
        }

        return false;
    }
}
