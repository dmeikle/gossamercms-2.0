package com.gossamercms.mvc.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record QueryOptions(
        int page,
        int size,
        Map<String,Object> filters,
        Map<String,String> orderBy,
        List<QueryFilter> queryFilters
) {

    // Backwards compatible constructor
    public QueryOptions(
            int page,
            int size,
            Map<String,Object> filters,
            Map<String,String> orderBy
    ) {
        this(
                page,
                size,
                filters,
                orderBy,
                List.of()
        );
    }

    public static QueryOptions of(
            int page,
            int size,
            Map<String, Object> filters,
            Map<String, String> orderBy
    ) {
        return new QueryOptions(
                page,
                size,
                filters,
                orderBy,
                List.of()
        );
    }


    public static Builder builder() {
        return new Builder();
    }


    // ---------------------------
    // Builder
    // ---------------------------
    public static class Builder {

        private int page = 1;

        private int size = 20;

        private Map<String, Object> filters = Map.of();

        private Map<String, String> orderBy = Map.of();

        private List<QueryFilter> queryFilters = List.of();


        public Builder page(int page) {
            this.page = page;
            return this;
        }


        public Builder size(int size) {
            this.size = size;
            return this;
        }


        public Builder filters(
                Map<String, Object> filters
        ) {
            this.filters = normalizeFilters(filters);
            return this;
        }


        public Builder orderBy(
                Map<String, String> orderBy
        ) {
            this.orderBy = orderBy;
            return this;
        }


        public Builder queryFilters(
                List<QueryFilter> queryFilters
        ) {
            this.queryFilters =
                    queryFilters == null
                            ? List.of()
                            : queryFilters;

            return this;
        }


        public QueryOptions build() {

            return new QueryOptions(
                    page,
                    size,
                    filters,
                    orderBy,
                    queryFilters
            );
        }


        // ⭐ Automatic type coercion
        private Map<String, Object> normalizeFilters(
                Map<String, Object> raw
        ) {

            if (raw == null || raw.isEmpty()) {
                return Map.of();
            }

            return raw.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> coerceValue(e.getValue())
                    ));
        }


        private Object coerceValue(
                Object value
        ) {

            if (value instanceof String s) {

                // ISO LocalDate
                if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {

                    return LocalDate.parse(s);
                }


                // ISO Instant
                try {

                    return Instant.parse(s);

                } catch (Exception ignored) {

                }


                return s;
            }


            return value;
        }
    }
}