package it.previnet.smartclaim.utils.query;

import java.util.Arrays;
import java.util.Collection;

public class Field implements Cloneable {

    public static final Field ONE = new Field("1");
    protected String alias;
    protected String name;
    protected Table table;
    private String function;


    public Field(String name) {
        this.name = name;
    }

    public static Field create(String name) {
        return new Field(name);
    }

    public static Condition eq(String fieldName, String leftAlias) {
        return Field.eq(fieldName, leftAlias, null);
    }

    public static Condition eq(String fieldName, String leftAlias, String rightAlias) {
        return Field.create(fieldName).of(leftAlias).eq(Field.create(fieldName).of(rightAlias));
    }

    public static Condition eq(String fieldName, Table leftTable) {
        return Field.eq(fieldName, leftTable, null);
    }

    public static Condition eq(String fieldName, Table leftAlias, Table rightAlias) {
        return Field.create(fieldName).of(leftAlias).eq(Field.create(fieldName).of(rightAlias));
    }

    public Field of(Table table) {
        this.table = table;
        return this;
    }

    public Field of(String tableAlias) {
        this.table = Table.create(null).as(tableAlias);
        return this;
    }

    public Field as(String alias) {
        this.alias = alias;
        return this;
    }

    public Condition eq(Field other) {
        return GenericCondition.eq(this, other);
    }


    public Condition like(Object s) {
        return GenericCondition.like(this, s);
    }

    public Condition isNull() {
        return GenericCondition.isNull(this);
    }

    public Condition isNotNull() {
        return GenericCondition.isNotNull(this);
    }

    public Condition gt(Field field) {
        return GenericCondition.gt(this, field);
    }

    public Condition lt(Field field) {
        return GenericCondition.lt(this, field);
    }


    public Condition inList(Collection params) {
        return GenericCondition.in(this, params);
    }

    public Condition in(Object... params) {
//        return this.in(Arrays.asList(params));
        return GenericCondition.in(this, params);
    }

    public Condition ne(Object val) {
        return GenericCondition.ne(this, val);
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Field clone() {
        Field f = new Field(this.name);
        f.alias = this.alias;
        f.table = this.table;
        return f;
    }

    @Override
    public String toString() {
        return this.toString(null);
    }

    public String toString(String alias) {
        String prefix = this.table != null && this.table.alias != null && !this.table.alias.isEmpty() ?
            this.table.alias + "." : "";
        String finalAlias = this.alias != null ? this.alias : alias;
        String postfix = finalAlias != null ? " AS " + finalAlias : "";

        String finalName = this.name;
        if (this.function != null && !this.function.isEmpty()) {
            prefix = this.function + "(" + prefix;
            finalName += ")";
        }

        return prefix + finalName + postfix;
    }


    public Field toUpper() {
        this.function = "upper";
        return this;
    }

    public Field toLower() {
        this.function = "lower";
        return this;
    }
}
