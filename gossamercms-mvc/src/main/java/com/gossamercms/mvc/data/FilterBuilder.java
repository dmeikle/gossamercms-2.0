package com.gossamercms.mvc.data;

import java.util.*;

public class FilterBuilder {

    private final Map<String, Object> filters = new LinkedHashMap<>();

    private FilterBuilder() {}

    public static FilterBuilder create() {
        return new FilterBuilder();
    }

    // ---------- Basic equality ----------
    public FilterBuilder eq(String field, Object value) {
        filters.put(field, Map.of("$eq", value));
        return this;
    }

    public FilterBuilder neq(String field, Object value) {
        filters.put(field, Map.of("$neq", value));
        return this;
    }

    // ---------- Comparison ----------
    public FilterBuilder gt(String field, Object value) {
        filters.put(field, Map.of("$gt", value));
        return this;
    }

    public FilterBuilder gte(String field, Object value) {
        filters.put(field, Map.of("$gte", value));
        return this;
    }

    public FilterBuilder lt(String field, Object value) {
        filters.put(field, Map.of("$lt", value));
        return this;
    }

    public FilterBuilder lte(String field, Object value) {
        filters.put(field, Map.of("$lte", value));
        return this;
    }

    // ---------- Contains / Like ----------
    public FilterBuilder contains(String field, String value) {
        filters.put(field, Map.of("$contains", value));
        return this;
    }

    public FilterBuilder startsWith(String field, String value) {
        filters.put(field, Map.of("$startsWith", value));
        return this;
    }

    public FilterBuilder endsWith(String field, String value) {
        filters.put(field, Map.of("$endsWith", value));
        return this;
    }

    // ---------- IN ----------
    public FilterBuilder in(String field, List<?> values) {
        filters.put(field, Map.of("$in", values));
        return this;
    }

    // ---------- Logical operators ----------
    public FilterBuilder and(FilterBuilder... builders) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FilterBuilder b : builders) {
            list.add(b.build());
        }
        filters.put("$and", list);
        return this;
    }

    public FilterBuilder or(FilterBuilder... builders) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FilterBuilder b : builders) {
            list.add(b.build());
        }
        filters.put("$or", list);
        return this;
    }

    public FilterBuilder not(FilterBuilder builder) {
        filters.put("$not", builder.build());
        return this;
    }

    // ---------- Nested fields ----------
    public FilterBuilder nested(String field, FilterBuilder nested) {
        filters.put(field, nested.build());
        return this;
    }

    // ---------- Final output ----------
    public Map<String, Object> build() {
        return Collections.unmodifiableMap(filters);
    }
}