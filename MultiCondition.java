package it.previnet.smartclaim.utils.query;

import java.util.ArrayList;
import java.util.List;

public class MultiCondition extends Condition {
    private List<Object> conditions = new ArrayList<>();
    private List<String> connectors = new ArrayList<>();

    MultiCondition(Object firstCondition) {
        this.conditions.add(firstCondition);
    }

    @Override
    public MultiCondition and(Object other) {
        return this.connect(other, "AND");
    }

    @Override
    public MultiCondition or(Object other) {
        return this.connect(other, "OR");
    }

    private MultiCondition connect(Object other, String connector) {
        Object cond = other;
        if (cond instanceof MultiCondition) {
            cond = GroupedCondition.create(other);
        }
        this.conditions.add(cond);
        this.connectors.add(connector);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (connectors.size() > 0) {
            for (int i = 0; i < connectors.size(); i++) {
                str.append(((Condition)conditions.get(i)).withDefaultAlias(this.defaultAlias)).append(" ")
                    .append(connectors.get(i)).append(" ");
            }
        } else {
            str.append(" ");
        }

        str.append(((Condition)conditions.get(conditions.size() - 1)).withDefaultAlias(this.defaultAlias));

        return str.toString().trim();
    }
}
