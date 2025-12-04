package src.ai;

public class SelectorNode extends CompositeNode {

    @Override
    public BehaviorStatus tick() {
        for (BehaviorNode child : children) {
            BehaviorStatus result = child.tick();

            if (result == BehaviorStatus.SUCCESS || result == BehaviorStatus.RUNNING) {
                return result;
            }
        }
        return BehaviorStatus.FAILURE;
    }
}
