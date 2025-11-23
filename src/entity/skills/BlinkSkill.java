package src.entity.skills;

import src.core.GamePanel;
import src.entity.Player;

public class BlinkSkill extends Skill {

    private static final int TILE_DISTANCE = 3;

    public BlinkSkill() {
        super(
                "blink",
                "Blink",
                "Teleport a few tiles toward the facing direction, skipping obstacles if possible.",
                180,
                0,
                25
        );
    }

    @Override
    protected void onActivate(Player player) {
        GamePanel gp = player.getGamePanel();
        if (gp == null || gp.collision == null) {
            return;
        }

        int stepSize = gp.tileSize;
        int dx = 0;
        int dy = 0;

        switch (player.getDirection()) {
            case "up":
                dy = -1;
                break;
            case "down":
                dy = 1;
                break;
            case "left":
                dx = -1;
                break;
            case "right":
                dx = 1;
                break;
            default:
                break;
        }

        if (dx == 0 && dy == 0) {
            return;
        }

        int targetX = player.worldX;
        int targetY = player.worldY;

        for (int i = 1; i <= TILE_DISTANCE; i++) {
            int nextX = player.worldX + dx * stepSize * i;
            int nextY = player.worldY + dy * stepSize * i;

            if (!gp.collision.willCollide(player, nextX, nextY)) {
                targetX = nextX;
                targetY = nextY;
            } else {
                break;
            }
        }

        player.worldX = targetX;
        player.worldY = targetY;
    }

    @Override
    public boolean canActivate(Player player) {
        return super.canActivate(player) && (player.getDirection() != null);
    }
}

