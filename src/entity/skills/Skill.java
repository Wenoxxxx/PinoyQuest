package src.entity.skills;

import src.entity.Player;

public abstract class Skill {

    private final String id;
    private final String name;
    private final String description;
    private final int cooldownFrames;
    private final int durationFrames;
    private final int energyCost;

    private int cooldownTimer = 0;
    private int remainingActiveFrames = 0;
    private boolean active = false;

    // === Independent cooldown override for this specific skill ===
    protected double cooldownMultiplier = 1.0;
    private long infiniteCooldownEndTime = 0;


    protected Skill(String id, String name, String description,
                    int cooldownFrames, int durationFrames, int energyCost) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.cooldownFrames = Math.max(0, cooldownFrames);
        this.durationFrames = Math.max(0, durationFrames);
        this.energyCost = Math.max(0, energyCost);
    }


    // ===========================================================
    // === Unified UPDATE (no duplicates, fully correct)
    // ===========================================================
    public void update(Player player) {

        // Update infinite cooldown buff
        updatePerSkillCooldownBuff();

        // Cooldown reduction
        if (cooldownTimer > 0) {
            cooldownTimer -= cooldownMultiplier;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }

        // Active effect system
        if (active) {

            whileActive(player);

            if (remainingActiveFrames > 0) {
                remainingActiveFrames--;
                if (remainingActiveFrames == 0) {
                    active = false;
                    onDeactivate(player);
                }
            }
        }
    }


    // ===========================================================
    // === Infinite cooldown system (per skill only)
    // ===========================================================
    public void enableInfiniteCooldown(long durationMs) {
        cooldownMultiplier = 0.0;
        infiniteCooldownEndTime = System.currentTimeMillis() + durationMs;
        System.out.println("[Skill Buff] " + name + " infinite cooldown for " + durationMs + "ms");
    }

    private void updatePerSkillCooldownBuff() {
        if (cooldownMultiplier == 0.0 &&
                System.currentTimeMillis() > infiniteCooldownEndTime) {

            cooldownMultiplier = 1.0;
            System.out.println("[Skill Buff] " + name + " cooldown restored");
        }
    }


    // ===========================================================
    // === Try Activate (uses per-skill cooldown only)
    // ===========================================================
    public boolean tryActivate(Player player) {

        if (!canActivate(player)) {
            return false;
        }

        if (energyCost > 0 && !player.consumeEnergy(energyCost)) {
            return false;
        }

        // Apply cooldown with per-skill multiplier
        cooldownTimer = (int)(cooldownFrames * cooldownMultiplier);

        if (durationFrames > 0) {
            remainingActiveFrames = durationFrames;
            active = true;
            onActivate(player);
        } else {
            onActivate(player);
            onDeactivate(player);
        }

        return true;
    }


    // ===========================================================
    // === Base methods
    // ===========================================================
    public boolean canActivate(Player player) {
        return !active && cooldownTimer == 0 && player.getEnergy() >= energyCost;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isOnCooldown() {
        return cooldownTimer > 0;
    }

    public int getCooldownTimer() {
        return cooldownTimer;
    }

    public int getCooldownFrames() {
        return cooldownFrames;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getDurationFrames() {
        return durationFrames;
    }

    public int getRemainingActiveFrames() {
        return remainingActiveFrames;
    }

    public int getElapsedActiveFrames() {
        return durationFrames - remainingActiveFrames;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getCooldownProgress() {
        if (cooldownFrames == 0) {
            return 0.0;
        }
        return (double) cooldownTimer / (double) cooldownFrames;
    }


    // Hooks for subclasses
    protected void whileActive(Player player) {}

    protected abstract void onActivate(Player player);

    protected void onDeactivate(Player player) {}
}
