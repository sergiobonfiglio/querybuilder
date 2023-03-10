package it.previnet.smartclaim.utils.query;

abstract public class Condition {
    protected String defaultAlias;

    public abstract MultiCondition and(Object other);

    public abstract MultiCondition or(Object other);

    public Condition withDefaultAlias(String alias) {
        this.defaultAlias = alias;
        return this;
    }
}
