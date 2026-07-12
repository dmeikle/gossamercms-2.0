package com.gossamercms.mvc.data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

public record QueryOptions(
        int page,
        int size,
        Map<String, Object> filters,
        Map<String, String> orderBy
) {
    public static QueryOptions of(
            int page,
            int size,
            Map<String, Object> filters,
            Map<String, String> orderBy
    ) {
        return new QueryOptions(page, size, filters, orderBy);
    }

    public static Builder builder() {
        return new Builder();
    }

    // ---------------------------
    //  Builder
    // ---------------------------
    public static class Builder {
        private int page = 1;
        private int size = 20;
        private Map<String, Object> filters = Map.of();
        private Map<String, String> orderBy = Map.of();

        public Builder() {
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder filters(Map<String, Object> filters) {
            this.filters = normalizeFilters(filters);
            return this;
        }

        public Builder orderBy(Map<String, String> orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public QueryOptions build() {
            return new QueryOptions(page, size, filters, orderBy);
        }

        // ⭐ Automatic type coercion
        private Map<String, Object> normalizeFilters(Map<String, Object> raw) {
            if (raw == null || raw.isEmpty()) return raw;

            return raw.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> coerceValue(e.getValue())
                    ));
        }

        private Object coerceValue(Object value) {
            if (value instanceof String s) {

                // ISO LocalDate (yyyy-MM-dd)
                if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return LocalDate.parse(s);
                }

                // ISO Instant (yyyy-MM-ddTHH:mm:ssZ or with microseconds)
                try {
                    return Instant.parse(s);
                } catch (Exception ignored) {
                }

                return s; // leave as string
            }

            return value;
        }
    }
}
/**
 * for date filters, the value can be:
 * "appointmentDate" -> LocalDate.of(2026, 7, 8)
 *
 * the column mapping can be:
 * Map.of("appointmentDate", "appointmentTime::date")
 *
 * so the where builder will generate:
 * appointmentTime::date = :appointmentDate
 */