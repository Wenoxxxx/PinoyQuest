package src.ai.nodes;

import src.ai.*;
import src.entity.mobs.Enemy;

public class AttackPlayerNode extends ActionNode {

    private final Enemy enemy;

    public AttackPlayerNode(Enemy enemy) {
        this.enemy = enemy;
    }

    @Override
    public BehaviorStatus tick() {

        if (enemy.isDead()) return BehaviorStatus.FAILURE;

        if (enemy.isInAttackRange()) {
            enemy.attack();
            return BehaviorStatus.SUCCESS;
        }

        return BehaviorStatus.FAILURE;
    }
}
