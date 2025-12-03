package src.ai;

import src.ai.nodes.*;
import src.entity.mobs.Enemy;

public class EnemyAI {

    private final BehaviorNode root;

    public EnemyAI(Enemy enemy) {

        // ATTACK sequence
        SequenceNode attack = new SequenceNode();
        attack.addChild(new CanSeePlayerNode(enemy))
              .addChild(new ConditionNode(() -> enemy.isInAttackRange()))
              .addChild(new AttackPlayerNode(enemy));

        // CHASE sequence
        SequenceNode chase = new SequenceNode();
        chase.addChild(new CanSeePlayerNode(enemy))
             .addChild(new MoveToPlayerNode(enemy));

        // PATROL fallback
        SequenceNode patrol = new SequenceNode();
        patrol.addChild(new HasPatrolPathNode(enemy))
              .addChild(new FollowPatrolPathNode(enemy));

        // Decision tree
        SelectorNode rootSelector = new SelectorNode();
        rootSelector.addChild(attack);
        rootSelector.addChild(chase);
        rootSelector.addChild(patrol);

        this.root = rootSelector;
    }

    public void update() {
        root.tick();
    }
}
