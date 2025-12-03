package src.ai.nodes;

import src.ai.*;
import src.entity.mobs.Enemy;

public class FollowPatrolPathNode extends ActionNode {

    private final Enemy enemy;

    public FollowPatrolPathNode(Enemy enemy) {
        this.enemy = enemy;
    }

    @Override
    public BehaviorStatus tick() {

        enemy.followPatrolPath();

        return BehaviorStatus.RUNNING;
    }
}
