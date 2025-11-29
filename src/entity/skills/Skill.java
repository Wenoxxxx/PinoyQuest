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
    // === NEW API: update WITH COOLDOWN MULTIPLIER ============
    // ===========================================================
    public void update(Player player, double cooldownMultiplier) {

        // Scale cooldown reduction by multiplier (0.0 = no cooldown, 1.0 = normal)
        if (cooldownTimer > 0) {
            cooldownTimer -= cooldownMultiplier;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }

        // Existing active effect logic (unchanged)
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

    // LEGACY SUPPORT (so your old skills still compile even if they call update(player))
    public void update(Player player) {
        update(player, 1.0);
    }

    // ===========================================================
    // === NEW API: tryActivate WITH COOLDOWN MULTIPLIER =========
    // ===========================================================
    public boolean tryActivate(Player player, double cooldownMultiplier) {

        if (!canActivate(player)) {
            return false;
        }

        if (energyCost > 0 && !player.consumeEnergy(energyCost)) {
            return false;
        }

        // Apply cooldown scaled by multiplier
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

    // Legacy support
    public boolean tryActivate(Player player) {
        return tryActivate(player, 1.0);
    }

    // ===========================================================
    // ==================== ORIGINAL METHODS ======================
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

    protected void whileActive(Player player) {}

    protected abstract void onActivate(Player player);

    protected void onDeactivate(Player player) {}
}
