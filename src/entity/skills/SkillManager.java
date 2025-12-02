package src.entity.skills;

import src.entity.Player;

public class SkillManager {

    private final Player player;
    private final Skill[] slots;

    public SkillManager(Player player) {
        this.player = player;

        this.slots = new Skill[]{
                new DashSkill(),
                new BlinkSkill(),
                new FocusSkill()
        };
    }

    // ===========================================================
    // UPDATE LOOP
    // ===========================================================
    public void update() {
        for (Skill skill : slots) {
            if (skill != null) {
                skill.update(player);   // per-skill cooldown logic
            }
        }
    }

    // ===========================================================
    // ACTIVATION
    // ===========================================================
    public boolean activateSlot(int slot) {

        if (slot < 0 || slot >= slots.length) return false;

        Skill skill = slots[slot];
        if (skill == null) return false;

        return skill.tryActivate(player);  // per-skill activation
    }

    // ===========================================================
    // ACCESSORS
    // ===========================================================
    public Skill getSkill(int slot) {
        if (slot < 0 || slot >= slots.length) return null;
        return slots[slot];
    }

    public Skill[] getSlots() {
        return slots.clone();
    }

    public int getSlotCount() {
        return slots.length;
    }
}
