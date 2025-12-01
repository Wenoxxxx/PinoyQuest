package src.entity.skills;

import src.entity.Player;

public class DashSkill extends Skill {

    private static final int SPEED_BONUS = 3;

    public DashSkill() {
        super(
                "dash",
                "Dash",
                "Gain a burst of movement speed for a short duration.",
                400,
                100,
                25
        );
    }

    @Override
    protected void onActivate(Player player) {
        player.addSpeedModifier(SPEED_BONUS);
    }

    @Override
    protected void onDeactivate(Player player) {
        player.addSpeedModifier(-SPEED_BONUS);
    }
}

