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

    public void update() {
        for (Skill skill : slots) {
            if (skill != null) {
                skill.update(player);
            }
        }
    }

    public boolean activateSlot(int slot) {
        Skill skill = getSkill(slot);
        if (skill == null) {
            return false;
        }
        return skill.tryActivate(player);
    }

    public Skill getSkill(int slot) {
        if (slot < 0 || slot >= slots.length) {
            return null;
        }
        return slots[slot];
    }

    public Skill[] getSlots() {
        return slots.clone();
    }

    public int getSlotCount() {
        return slots.length;
    }
}

