package it.previnet.smartclaim.utils.query;

import java.util.*;
import java.util.stream.Collectors;

public class QueryDSL {

    protected List<Field> fields = new ArrayList<>();
    protected List<Field> distinctFields = new ArrayList<>();
    protected Integer limit;
    protected Table from;
    protected List<Join> joinedTable = new ArrayList<>();

    protected Condition where = null;
    protected Field orderBy;

    protected String randBaseAlias;
    protected int currentAliasNum = 0;
    private String orderByType;

    public QueryDSL() {
        this.randBaseAlias = "t" + leftPad(String.valueOf((int) (Math.random() * 1000)), 3) + "_";
    }

    private static String leftPad(String str, int size) {
        String padChar = "0";
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < size; i++) {
            pad.append(padChar);
        }
        String res = pad + str;

        return res.substring(res.length() - size, res.length());
    }

    public static QueryDSL create() {
        return new QueryDSL();
    }

    public QueryDSL select(Collection<Field> fields) {
        this.fields.addAll(fields);
        return this;
    }

    public QueryDSL select(Field... fields) {
        this.select(Arrays.asList(fields));
        return this;
    }

    public QueryDSL selectDistinct(Collection<Field> fields) {
        this.distinctFields.addAll(fields);
        return this;
    }

    public QueryDSL selectDistinct(Field... fields) {
        this.selectDistinct(Arrays.asList(fields));
        return this;
    }

    public QueryDSL limit(int limit) {
        this.limit = limit;
        return this;
    }

    public QueryDSL from(Table table) {
        this.from = table;
        return this;
    }

    public QueryDSL from(String table) {
        return this.from(Table.create(table));
    }

    public QueryDSL leftJoin(QueryDSL table) {
        this.join("LEFT", null, table);
        return this;
    }

    public QueryDSL leftJoin(Table table) {
        this.join("LEFT", table.alias, table);
        return this;
    }

    public QueryDSL leftJoin(String table) {
        return this.leftJoin(Table.create(table));
    }

    public QueryDSL join(Table table) {
        this.join(null, table.alias, table);
        return this;
    }

    public QueryDSL join(String table) {
        return this.join(Table.create(table));
    }

    private <T> void join(String joinType, String alias, T table) {
        String joinAlias = alias != null ? alias : nextAlias();
        Join join = new Join(joinType, joinAlias, table);
        this.joinedTable.add(join);
    }

    private String nextAlias() {
        return this.randBaseAlias + this.currentAliasNum++;
    }

    public QueryDSL on(Condition joinCondition) {

        Join lastJoin = this.joinedTable.get(this.joinedTable.size() - 1);
        lastJoin.joinCondition = joinCondition.withDefaultAlias(lastJoin.alias);

        return this;
    }

    public QueryDSL as(String alias) {

        Join lastJoin = this.joinedTable.get(this.joinedTable.size() - 1);
        if (lastJoin != null) {
            lastJoin.alias = alias;
        } else if (this.from != null) {
            this.from.alias = alias;
        } else {
            throw new RuntimeException();
        }

        return this;
    }

    public QueryDSL where(Condition condition) {
        this.where = condition;
        return this;
    }

    public QueryDSL orderBy(Field field) {
        return this.orderBy(field, "ASC");
    }

    public QueryDSL orderByDesc(Field field) {
        return this.orderBy(field, "DESC");
    }

    private QueryDSL orderBy(Field field, String type) {

        if (field.alias != null) {
            //the order by should only contain the alias
            this.orderBy = Field.create(field.alias).of(field.table);
        } else {
            //remove alias (not needed for the order by clause)
            this.orderBy = field.clone();
            this.orderBy.alias = null;
        }

        this.orderByType = type;

        return this;
    }

    public static Condition exists(QueryDSL b) {
        return new GenericCondition<>(null, GroupedCondition.create(b), "EXISTS ");
    }

    public static Condition notExists(QueryDSL b) {
        return new GenericCondition<>(null, GroupedCondition.create(b), "NOT EXISTS ");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        //SELECT
        sb.append("SELECT ");

        //DISTINCT
        List<Field> fieldsToPrint = this.fields;
        if (this.distinctFields.size() > 0) {
            fieldsToPrint = this.distinctFields;
            sb.append("DISTINCT ");
        }

        //TOP
        if (this.limit != null) {
            sb.append("TOP " + this.limit + " ");
        }

        //FIELDS
        String fieldsStr = String.join(",", fieldsToPrint.stream().map(Field::toString).collect(Collectors.toList()));
        if (fieldsStr.length() == 0) {
            fieldsStr = "*";
        }
        sb.append(fieldsStr);

        //FROM
        sb.append(" FROM ");
        sb.append(this.from);

        //JOIN
        if (this.joinedTable.size() > 0) {
            sb.append(" ");
            sb.append(String.join(" ", this.joinedTable.stream()
                .map(join -> join.toString() +
                    " ON " + join.joinCondition.withDefaultAlias(join.alias))
                .collect(Collectors.toList())));
        }

        //WHERE
        if (this.where != null) {
            sb.append(" WHERE " + this.where);
        }

        //ORDER BY
        if (this.orderBy != null) {
            sb.append(" ORDER BY " + this.orderBy + " " + this.orderByType);
        }

        return sb.toString();
    }

}


