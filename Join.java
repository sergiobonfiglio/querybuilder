package it.previnet.smartclaim.utils.query;

class Join<T> {
    private String joinType;
    protected T source;
    protected Condition joinCondition;
    protected String alias;

    public Join(String alias, T source) {
        this(null, alias, source);
    }

    public Join(String joinType, String alias, T source) {
        this.joinType = joinType;
        this.source = source;
        this.alias = alias;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String prefix = this.joinType != null ? " " + this.joinType : "";

        sb.append(prefix);
        sb.append(" JOIN ");

        if (source instanceof QueryDSL) {
            sb.append("(" + source + ") AS " + this.alias);
        } else {
            //Table
            Table t = (Table) source;
            sb.append(source);
            if (t.alias != null && !t.alias.equalsIgnoreCase(this.alias)) {
                //error!
                throw new RuntimeException();
            } else if (t.alias == null) {
                sb.append(" AS " + this.alias);
            }
        }

        return sb.toString().trim();
    }
}
