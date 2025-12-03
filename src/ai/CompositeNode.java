package src.ai;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeNode extends BehaviorNode {

    protected List<BehaviorNode> children = new ArrayList<>();

    public CompositeNode addChild(BehaviorNode child) {
        children.add(child);
        return this;
    }
}
