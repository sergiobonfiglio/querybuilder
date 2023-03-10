package it.previnet.smartclaim.utils.query;

public class Table {

    protected String alias;
    protected String name;
    protected Field fields;

    private Table(String table) {
        this.name = table;
    }

    public static Table create(String table) {
        return new Table(table);
    }

    public static Table alias(String alias) {
        return new Table("").as(alias);
    }

    public Table as(String alias) {
        this.alias = alias;
        return this;
    }

    public Field field(String fieldName) {
        return Field.create(fieldName).of(this);
    }

    public Field allFields() {
        return Field.create("*").of(this);
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean withAlias) {
        String postfix = withAlias && this.alias != null ? " AS " + this.alias : "";

        return this.name + postfix;
    }
}
