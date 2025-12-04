package src.ai.nodes;

import src.ai.*;
import src.entity.mobs.Enemy;

public class MoveToPlayerNode extends ActionNode {

    private final Enemy enemy;

    public MoveToPlayerNode(Enemy enemy) {
        this.enemy = enemy;
    }

    @Override
    public BehaviorStatus tick() {

        if (enemy.isDead()) return BehaviorStatus.FAILURE;

        enemy.moveTowards(enemy.getPlayer());

        return BehaviorStatus.RUNNING; // continues chasing
    }
}
