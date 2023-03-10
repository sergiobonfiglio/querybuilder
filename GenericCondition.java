package it.previnet.smartclaim.utils.query;

import it.previnet.smartclaim.utils.ListUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GenericCondition<T1, T2> extends Condition {
    private T1 first;
    private T2 second;
    private String compareSymbol;

    GenericCondition(T1 first, T2 second, String compareSymbol) {
        this.first = sanitize(first);
        this.second = sanitize(second);
        this.compareSymbol = compareSymbol;
    }

    private static <T> T sanitize(T f) {
        if (f instanceof String) {
            return (T) ("'" + f + "'");
        } else {
            return f;
        }
    }

    public static <T1, T2> Condition eq(T1 a, T2 b) {
        return new GenericCondition<>(a, b, "=");
    }

    public static <T1, T2> Condition like(T1 a, T2 b) {
        return new GenericCondition<>(a, b, " LIKE ");
    }

    public static <T1> Condition isNull(T1 a) {
        return new GenericCondition<>(a, null, " IS NULL");
    }

    public static <T1> Condition isNotNull(T1 a) {
        return new GenericCondition<>(a, null, " IS NOT NULL");
    }

    public static <T1, T2> Condition gt(T1 a, T2 b) {
        return new GenericCondition<>(a, b, ">");
    }

    public static <T1, T2> Condition lt(T1 a, T2 b) {
        return new GenericCondition<>(a, b, "<");
    }

    public static <T1, T2> Condition in(T1 a, T2... params) {
        return GenericCondition.in(a, Arrays.asList(params));
    }

    public static <T1, T2> Condition in(T1 a, Collection<T2> params) {

        List<String> strList;
//        if (params instanceof String[]) {
//            strList = Arrays.asList((String[]) params);
//        } else {
//            strList = Arrays.stream(params).map(p -> {
            strList = params.stream().map(p -> {
                if (p instanceof String) {
                    return "'" + p + "'";
                } else {
                    return String.valueOf(p);
                }
            }).collect(Collectors.toList());
//        }

        String strIn = String.join(",", strList);

        return new GenericCondition<>(a, Field.create("(" + strIn + ")"), " IN ");
    }

    public static <T1, T2> Condition ne(T1 left, T2 right) {
        return new GenericCondition<>(left, right, "<>");
    }

    @Override
    public MultiCondition and(Object other) {
        return new MultiCondition(this).and(other);
    }

    @Override
    public MultiCondition or(Object other) {
        return new MultiCondition(this).or(other);
    }

    private Object addDefaultAlias(Object object) {
        Object result = object;
        if (object instanceof Field) {
            Field field = ((Field) object).clone();
            if (field.table == null) {
                field.table = Table.create(null);
            }
            if (field.table.alias == null) {
                field.table.alias = this.defaultAlias;
                result = field;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return (first != null ? addDefaultAlias(first).toString() : "") +
            (compareSymbol != null ? compareSymbol : "") +
            (second != null ? addDefaultAlias(second).toString() : "");
    }
}
