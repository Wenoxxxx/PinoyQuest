package src.entity.skills;

import src.entity.Player;

public class SkillManager {

    private final Player player;
    private final Skill[] slots;

    // === GLOBAL COOLDOWN MULTIPLIER ===
    // 1.0 = normal cooldown
    // 0.0 = no cooldown
    private double globalCooldownMultiplier = 1.0;

    // === TIMER FOR NO-COOLDOWN BUFF ===
    private int noCooldownTimer = 0;

    public SkillManager(Player player) {
        this.player = player;
        // instantiate using the skills' no-arg constructors
        this.slots = new Skill[]{
                new DashSkill(),
                new BlinkSkill(),
                new FocusSkill()
        };
    }

    // =============================
    // BUFF / MULTIPLIER MANAGEMENT
    // =============================

    public void setGlobalMultiplier(double value) {
        this.globalCooldownMultiplier = value;
    }

    public double getGlobalMultiplier() {
        return globalCooldownMultiplier;
    }

    public void activateNoCooldown(int ticks) {
        this.noCooldownTimer = ticks;
        this.globalCooldownMultiplier = 0.0;
    }

    // =============================
    // UPDATE LOOP
    // =============================
    public void update() {
        // Process no-cooldown timer
        if (noCooldownTimer > 0) {
            noCooldownTimer--;
            if (noCooldownTimer == 0) {
                globalCooldownMultiplier = 1.0; // reset
            }
        }

        // Update each skill (pass player + multiplier)
        for (Skill skill : slots) {
            if (skill != null) {
                skill.update(player, globalCooldownMultiplier);
            }
        }
    }

    // =============================
    // ACTIVATION + ACCESSORS
    // =============================

    public boolean activateSlot(int slot) {
        if (slot < 0 || slot >= slots.length) {
            return false; // prevent crash
        }

        Skill skill = slots[slot];
        if (skill == null) return false;

        return skill.tryActivate(player, globalCooldownMultiplier);
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
