package it.previnet.smartclaim.utils.query;

public class GroupedCondition extends Condition {

    private Object condition;

    private GroupedCondition(Object condition) {
        this.condition = condition;
    }

    public static GroupedCondition create(Object condition) {
        return new GroupedCondition(condition);
    }

    @Override
    public MultiCondition and(Object other) {
        return new MultiCondition(this).and(other);
    }

    @Override
    public MultiCondition or(Object other) {
        return new MultiCondition(this).or(other);
    }

    @Override
    public String toString() {
        return "(" + this.condition.toString() + ")";
    }
}
