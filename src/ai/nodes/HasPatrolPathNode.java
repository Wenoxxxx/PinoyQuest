package src.ai.nodes;

import src.ai.ConditionNode;
import src.entity.mobs.Enemy;

public class HasPatrolPathNode extends ConditionNode {

    public HasPatrolPathNode(Enemy enemy) {
        super(enemy::hasPatrolPath);
    }
}
