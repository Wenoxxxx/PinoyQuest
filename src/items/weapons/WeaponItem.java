package src.items.weapons;

import src.core.GamePanel;
import src.items.Item;
import src.util.SpriteLoader;

public abstract class WeaponItem extends Item {

    protected int damage;
    protected float attackSpeed;   // attacks per second
    protected boolean equippable = true;

    public WeaponItem(
            GamePanel gp,
            int x,
            int y,
            String name,
            String description,
            int damage,
            float attackSpeed,
            String spritePath
    ) {
        super(gp, x, y, name, description);

        this.damage = damage;
        this.attackSpeed = attackSpeed;

        // Load sprite using your project's sprite loader
        this.sprite = SpriteLoader.load(spritePath);
        this.isConsumable = false;   // WEAPONS ARE NOT CONSUMABLE
        this.consumed = false;       // NEVER auto-remove from inventory
    }

    public int getDamage() {
        return damage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }
}
