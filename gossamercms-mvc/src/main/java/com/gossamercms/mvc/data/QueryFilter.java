package com.gossamercms.mvc.data;

public record QueryFilter(
        String field,
        Operator operator,
        Object value
) {

    public enum Operator {
        EQUALS,
        LIKE,
        ILIKE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL
    }


    public static QueryFilter equals(
            String field,
            Object value
    ) {
        return new QueryFilter(
                field,
                Operator.EQUALS,
                value
        );
    }


    public static QueryFilter like(
            String field,
            String value
    ) {
        return new QueryFilter(
                field,
                Operator.LIKE,
                "%" + value + "%"
        );
    }


    public static QueryFilter ilike(
            String field,
            String value
    ) {
        return new QueryFilter(
                field,
                Operator.ILIKE,
                "%" + value + "%"
        );
    }


    public static QueryFilter isNull(
            String field
    ) {
        return new QueryFilter(
                field,
                Operator.IS_NULL,
                null
        );
    }
}