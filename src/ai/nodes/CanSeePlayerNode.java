package src.ai.nodes;

import src.ai.ConditionNode;
import src.entity.mobs.Enemy;

public class CanSeePlayerNode extends ConditionNode {

    public CanSeePlayerNode(Enemy enemy) {
        super(enemy::canSeePlayer);
    }
}
