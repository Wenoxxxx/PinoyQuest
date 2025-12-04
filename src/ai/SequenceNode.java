package src.ai;

public class SequenceNode extends CompositeNode {

    @Override
    public BehaviorStatus tick() {
        for (BehaviorNode child : children) {
            BehaviorStatus result = child.tick();

            if (result == BehaviorStatus.FAILURE) {
                return BehaviorStatus.FAILURE;
            }

            if (result == BehaviorStatus.RUNNING) {
                return BehaviorStatus.RUNNING;
            }
        }
        return BehaviorStatus.SUCCESS;
    }
}
