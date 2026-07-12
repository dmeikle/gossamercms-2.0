package com.gossamercms.mvc.models;

import java.util.*;
import java.time.Instant;

public final class ModelMeta {

    private final String table;
    private final Map<String, Class<?>> columns;
    private final String datasourceKey;
    private final String defaultSort;
    private final Map<String, Integer> lengths;

    private ModelMeta(
            String table,
            Map<String, Class<?>> columns,
            String datasourceKey,
            String defaultSort,
            Map<String, Integer> lengths
    ) {
        this.table = table;
        this.columns = Collections.unmodifiableMap(columns);
        this.datasourceKey = datasourceKey;
        this.defaultSort = defaultSort;
        this.lengths = Collections.unmodifiableMap(lengths);
    }

    public String table() { return table; }
    public String defaultSort() { return defaultSort; }
    public Map<String, Class<?>> columns() { return columns; }
    public String datasourceKey() { return datasourceKey; }
    public Integer length(String name) { return lengths.get(name); }

    public static ModelMeta of(Class<?> modelClass) {
        try {
            var field = modelClass.getDeclaredField("META");
            field.setAccessible(true);
            Object value = field.get(null);

            if (!(value instanceof ModelMeta meta)) {
                throw new IllegalStateException("META field in " + modelClass.getName() + " is not a ModelMeta");
            }

            return meta;

        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "Model class " + modelClass.getName() + " must define: public static final ModelMeta META", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load ModelMeta from " + modelClass.getName(), e);
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String table;
        private String datasourceKey;
        private final Map<String, Class<?>> columns = new LinkedHashMap<>();
        private final Map<String, Integer> lengths = new HashMap<>();
        private String defaultSort;

        public Builder table(String table) { this.table = table; return this; }

        public Builder column(String name, Class<?> type) {
            columns.put(name, type);
            return this;
        }

        public Builder column(String name, Class<?> type, int length) {
            columns.put(name, type);
            lengths.put(name, length);
            return this;
        }

        public Builder datasource(String key) { this.datasourceKey = key; return this; }
        public Builder defaultSort(String defaultSort) { this.defaultSort = defaultSort; return this; }

        public ModelMeta build() {
            return new ModelMeta(
                    table,
                    columns,
                    datasourceKey,
                    defaultSort,
                    lengths
            );
        }
    }

    public static ModelMeta.Builder builderWithId(String table) {
        return ModelMeta.builder()
                .table(table)
                .datasource("postgres")
                .column("id", UUID.class, 100);
    }

    public static ModelMeta.Builder auditable(String table) {
        return builderWithId(table)
                .column("createdAt", Instant.class);
    }

    public static ModelMeta.Builder updateable(String table) {
        return builderWithId(table)
                .column("createdAt", Instant.class)
                .column("updatedAt", Instant.class);
    }
}