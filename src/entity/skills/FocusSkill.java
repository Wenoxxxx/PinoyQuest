package src.entity.skills;

import src.entity.Player;

public class FocusSkill extends Skill {

    private static final int REGEN_INTERVAL_FRAMES = 20;
    private static final int ENERGY_PER_TICK = 3;
    private static final int HEALTH_PER_TICK = 1;

    public FocusSkill() {
        super(
                "focus",
                "Focus",
                "Channel inner energy to slowly heal and restore stamina.",
                360,
                180,
                0
        );
    }

    @Override
    protected void onActivate(Player player) {
        player.setChanneling(true);
    }

    @Override
    protected void whileActive(Player player) {
        if (getElapsedActiveFrames() % REGEN_INTERVAL_FRAMES == 0) {
            player.restoreEnergy(ENERGY_PER_TICK);
            player.heal(HEALTH_PER_TICK);
        }
    }

    @Override
    protected void onDeactivate(Player player) {
        player.setChanneling(false);
    }
}

