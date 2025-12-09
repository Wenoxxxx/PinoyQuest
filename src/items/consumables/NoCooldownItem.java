package src.items.consumables;

import java.io.File;
import javax.imageio.ImageIO;

import src.core.GamePanel;
import src.entity.Player;
import src.items.Item;
import src.entity.skills.Skill;

public class NoCooldownItem extends Item {

    // 5 seconds effect at 60 FPS → but since we now use MS time,
    // convert ticks to milliseconds: 300 ticks ≈ 5000 ms
    private static final long DURATION_MS = 5000;

    public NoCooldownItem(GamePanel gp, int x, int y) {
        super(gp, x, y, "No Cooldown Potion", "Removes cooldown from all skills temporarily.");

        try {
            sprite = ImageIO.read(new File("assets/items/1nocd.png"));
        } catch (Exception e) {
            System.out.println("ERROR: Failed loading no cooldown sprite");
        }

        this.widthTiles = 2;
        this.heightTiles = 2;

        this.pickupWidth = gp.tileSize;
        this.pickupHeight = gp.tileSize;
        this.pickupOffsetX = 0;
        this.pickupOffsetY = 0;
    }

    @Override
    public void onPickup() {
        System.out.println("Picked up: " + name);
    }

    @Override
    public void use(Player player) {

        System.out.println("[ITEM] Applying NO COOLDOWN to ALL SKILLS for " + DURATION_MS + " ms!");

        // Apply infinite cooldown to every skill independently
        for (Skill s : player.getSkillManager().getSlots()) {
            if (s != null) {
                s.enableInfiniteCooldown(DURATION_MS);
            }
        }

        consumed = true;
    }
}
