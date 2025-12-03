package src.ai;

import java.util.function.BooleanSupplier;

public class ConditionNode extends BehaviorNode {

    private final BooleanSupplier condition;

    public ConditionNode(BooleanSupplier condition) {
        this.condition = condition;
    }

    @Override
    public BehaviorStatus tick() {
        return condition.getAsBoolean()
                ? BehaviorStatus.SUCCESS
                : BehaviorStatus.FAILURE;
    }
}
