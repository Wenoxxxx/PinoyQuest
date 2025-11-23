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

    public void update(Player player) {
        if (cooldownTimer > 0) {
            cooldownTimer--;
        }

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

    public boolean tryActivate(Player player) {
        if (!canActivate(player)) {
            return false;
        }

        if (energyCost > 0 && !player.consumeEnergy(energyCost)) {
            return false;
        }

        cooldownTimer = cooldownFrames;

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

