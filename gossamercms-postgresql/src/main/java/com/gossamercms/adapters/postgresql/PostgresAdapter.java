package com.gossamercms.adapters.postgresql;


import com.gossamercms.mvc.data.DataSourceAdapter;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.mvc.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostgresAdapter implements DataSourceAdapter {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(PostgresAdapter.class);

    @Override
    public String key() {
        return "postgres";
    }

    // ------------------------------------------------------------
    // SAVE (Entity only)
    // ------------------------------------------------------------
    @Override
    public void save(String table, Map<String, Object> params) {

        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Cannot insert empty params");
        }

        // Generate ID if missing
        if (!params.containsKey("id") || params.get("id") == null) {
            params.put("id", UUID.randomUUID());
        }

        // Only include non-null values
        Map<String, Object> effective = params.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<String> columns = new ArrayList<>(effective.keySet());

        String columnList = columns.stream()
                .map(this::quote)
                .collect(Collectors.joining(", "));

        String placeholders = columns.stream()
                .map(col -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + quote(table) + " (" + columnList + ") VALUES (" + placeholders + ")";

        jdbc.update(sql, ps -> {
            int index = 1;

            for (String col : columns) {
                Object value = effective.get(col);

                if (value instanceof Instant instant) {
                    ps.setTimestamp(index, Timestamp.from(instant));
                } else if (value instanceof org.postgresql.util.PGobject pg) {
                    ps.setObject(index, pg);
                } else {
                    ps.setObject(index, value);
                }

                index++;
            }
        });
    }



        @Override
    public Object find(String id) {
        return null;
    }

    // ------------------------------------------------------------  changing read/write to use the new reflectionutils classes
    // FIND ONE
    // ------------------------------------------------------------
    @Override
    public Object findOne(String table, Map<String, Object> filter) {
        return findOne(table, filter, Collections.emptyMap());
    }

    public Object findOne(String table,
                          Map<String, Object> filter,
                          Map<String, String> columnMappings) {

        String where = buildWhere(filter, columnMappings);

        String sql = "SELECT * FROM " + table;

        if (!where.isBlank()) {
            sql += " WHERE " + where;
        }

        sql += " LIMIT 1";

        List<Map<String, Object>> rows =
                namedJdbcTemplate.queryForList(sql, filter);

        return rows.isEmpty() ? null : rows.getFirst();
    }


    private boolean hasWhereClause(String sql) {
        return sql != null && (sql.toLowerCase().contains("where ") ||
                sql.contains("/*WHERE_CLAUSE*/"));
    }

    @Override
    public Object findOneIncludingDeleted(String table, Map<String, Object> filter) {
        return findOne(table, filter);
    }

    // ------------------------------------------------------------
    // UPDATE (Entity only)
    // ------------------------------------------------------------
    @Override
    public Map<String, Object> update(String table, UUID id, Map<String, Object> params) {

            if (params == null || params.isEmpty()) {
                return null; // nothing to update
            }

            // Remove id if someone accidentally passed it
            Map<String, Object> effective = params.entrySet().stream()
                    .filter(e -> e.getValue() != null)          // 🔥 only non-null values
                    .filter(e -> !"id".equals(e.getKey()))      // 🔥 never update primary key
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

            if (effective.isEmpty()) {
                return null; // nothing to update after filtering
            }

            List<String> columns = new ArrayList<>(effective.keySet());

            String setClause = columns.stream()
                    .map(col -> "\"" + col + "\" = ?")
                    .collect(Collectors.joining(", "));

            String sql = "UPDATE \"" + table + "\" SET " + setClause + " WHERE \"id\" = ? RETURNING *";

            List<Map<String, Object>> rows = jdbc.query(
                    sql,
                    ps -> {
                        int index = 1;

                        for (String col : columns) {
                            Object value = effective.get(col);

                            if (value instanceof Instant instant) {
                                ps.setTimestamp(index, Timestamp.from(instant));
                            } else if (value instanceof org.postgresql.util.PGobject pg) {
                                ps.setObject(index, pg);   // JSONB
                            } else {
                                ps.setObject(index, value);
                            }

                            index++;
                        }

                        ps.setObject(index, id);
                    },
                    (rs, rowNum) -> ReflectionUtils.mapRowToMap(rs)
            );

            return rows.isEmpty() ? null : rows.getFirst();
        }






        // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    @Override
    public void delete(String table, UUID id) {
        jdbc.update("DELETE FROM " + table + " WHERE id = ?", id);
    }

    @Override
    public void bulkDelete(String table, Collection<UUID> ids) {
        jdbc.execute("UPDATE " + table + " SET deletedAt = now() WHERE id IN (" + ids.stream() + ")");
    }

    // ------------------------------------------------------------
    // RESTORE
    // ------------------------------------------------------------
    @Override
    public Object restoreById(String table, UUID id, UUID restoredBy) {
        String sql = "UPDATE " + table + " SET deleted = false WHERE id = ? RETURNING *";
        List<Map<String, Object>> rows = jdbc.queryForList(sql, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    // ------------------------------------------------------------
    // COUNT
    // ------------------------------------------------------------
    @Override
    public long count(String table, Map<String, Object> filter) {
        String where = buildWhere(filter, Collections.emptyMap());

        String sql = "SELECT COUNT(*) FROM " + table;

        if (where != null && !where.isBlank()) {
            sql += " WHERE " + where;
        }

        return namedJdbcTemplate.queryForObject(
                sql,
                filter,
                Long.class
        );
    }

    // ------------------------------------------------------------
    // FIND MANY
    // ------------------------------------------------------------
    @Override
    public ListResultset<?> findMany(
            ModelMeta meta,
            Map<String, Object> filter,
            Map<String, String> orderBy,
            int page,
            int size
    ) {
        page = Math.max(1, page);
        size = Math.max(1, size);
        int offset = (page - 1) * size;

        String where = buildWhere(filter,
                Collections.emptyMap());

        String orderClause = "";
        if (!orderBy.isEmpty()) {
            orderClause = " ORDER BY " + orderBy.entrySet().stream()
                    .map(e -> quote(e.getKey()) + " " + e.getValue())
                    .collect(Collectors.joining(", "));
        } else if (meta.defaultSort() != null && !meta.defaultSort().isBlank()) {
            String[] parts = meta.defaultSort().split("\\s+");
            orderClause = " ORDER BY " + quote(parts[0]) + " " + parts[1];
        }

        String sql = "SELECT * FROM " + meta.table();

        if (where != null && !where.isBlank()) {
            sql += " WHERE " + where;
        }

        sql += orderClause;
        sql += " LIMIT " + size;
        sql += " OFFSET " + offset;

        System.out.println(sql);

        List<Map<String, Object>> rows = namedJdbcTemplate.queryForList(sql, filter); //jdbc.queryForList(sql, filter.values().toArray());
        long total = count(meta.table(), filter);

        return ListResultset.of(rows, page, size, total);
    }

    @Override
    public boolean exists(String table, UUID id) {
        return DataSourceAdapter.super.exists(table, id);
    }

    // ------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------
    public String buildWhere(
            Map<String, Object> filters,
            Map<String, String> columnMappings
    ) {
        if (filters == null || filters.isEmpty()) {
            return "";
        }

        // Identify Start/End pairs
        Map<String, Object> startMap = new HashMap<>();
        Map<String, Object> endMap = new HashMap<>();

        filters.forEach((key, value) -> {
            if (key.endsWith("Start")) {
                String base = key.substring(0, key.length() - "Start".length());
                startMap.put(base, value);
            } else if (key.endsWith("End")) {
                String base = key.substring(0, key.length() - "End".length());
                endMap.put(base, value);
            }
        });

        // Build normal filters (excluding Start/End keys)
        String normalFilters = filters.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .filter(e -> !"page".equals(e.getKey()))
                .filter(e -> !"size".equals(e.getKey()))
                .filter(e -> !"sortBy".equals(e.getKey()))
                .filter(e -> !"direction".equals(e.getKey()))
                .filter(e -> {
                    String key = e.getKey();
                    String base = key.endsWith("Start") ? key.substring(0, key.length() - 5)
                            : key.endsWith("End") ? key.substring(0, key.length() - 3)
                              : key;

                    // Skip Start/End keys because BETWEEN handles them
                    return !(startMap.containsKey(base) || endMap.containsKey(base));
                })
                .map(e -> {
                    String key = e.getKey();

                    // Determine base key (strip Start/End)
                    String base = key.endsWith("Start") ? key.substring(0, key.length() - 5)
                            : key.endsWith("End") ? key.substring(0, key.length() - 3)
                              : key;

                    // Use base key for columnMappings
                    String mapped = columnMappings.getOrDefault(base, base);

                    boolean isExpression = mapped.contains("::") || mapped.contains(" ");

                    String column;
                    if (mapped.contains("::")) {
                        // Split expression: column::type
                        String[] parts = mapped.split("::", 2);

                        // Quote the column name
                        String quotedColumn = quote(parts[0]);

                        // Reassemble expression
                        column = quotedColumn + "::" + parts[1];
                    } else {
                        // Normal column
                        column = quote(mapped);
                    }

                    return column + " = :" + key;
                })

                .collect(Collectors.joining(" AND "));

        // Build BETWEEN filters
        String betweenFilters = startMap.entrySet().stream()
                .filter(e -> endMap.containsKey(e.getKey()))
                .map(e -> {
                    String base = e.getKey();
                    String mapped = columnMappings.getOrDefault(base, base);

                    String column;

                    if (mapped.contains("::")) {
                        // Handle column::type
                        String[] parts = mapped.split("::", 2);
                        column = quote(parts[0]) + "::" + parts[1];
                    } else {
                        column = quote(mapped);
                    }

                    return column + " BETWEEN :" + base + "Start AND :" + base + "End";
                })
                .collect(Collectors.joining(" AND "));



        // Combine both
        if (!normalFilters.isEmpty() && !betweenFilters.isEmpty()) {
            return normalFilters + " AND " + betweenFilters;
        }
        if (!normalFilters.isEmpty()) {
            return normalFilters;
        }
        return betweenFilters;
    }

    /**
     * Quote a column name.
     */
    private String quote(String column) {
        if (column == null) return null;

        // already safely quoted manually → don't touch
        if (column.contains("\"") && !column.contains(".")) {
            return column;
        }

        // handle qualified columns: uc.contextType
        if (column.contains(".")) {
            return Arrays.stream(column.split("\\."))
                    .map(part -> "\"" + part + "\"")
                    .collect(Collectors.joining("."));
        }

        // simple column
        return "\"" + column + "\"";
    }

    private Object[] append(Object[] arr, Object value) {
        Object[] newArr = Arrays.copyOf(arr, arr.length + 1);
        newArr[arr.length] = value;
        return newArr;
    }
    private Object convertValue(Object value) {
        if (value instanceof Instant instant) {
            return java.sql.Timestamp.from(instant);
        }
        return value;
    }


    public <T> ListResultset<T> findAllBySql(
            String sql,
            String countSql,
            Map<String, Object> params,
            Map<String, String> columnMappings,
            Map<String, String> orderBy,
            int page,
            int size,
            RowMapper<T> mapper
    ) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        int offset = (page - 1) * size;

        Map<String, Object> queryParams = new HashMap<>(params);
        queryParams.put("limit", size);
        queryParams.put("offset", offset);

        // Build WHERE clause
        // Build WHERE clause
        String whereClause = buildWhere(params, columnMappings);

        if (!whereClause.isBlank()) {

            if (sql.contains("/*WHERE_CLAUSE*/")) {
                // Replace placeholder
                sql = sql.replace("/*WHERE_CLAUSE*/", " WHERE " + whereClause);
                countSql = countSql.replace("/*WHERE_CLAUSE*/", " WHERE " + whereClause);

            } else if (hasWhereClause(sql)) {
                // SQL already has WHERE → append AND
                sql += " AND " + whereClause;
                countSql += " AND " + whereClause;

            } else {
                // SQL has no WHERE → insert WHERE before GROUP BY / HAVING / ORDER BY
                sql = insertWhereClause(sql, whereClause);
                countSql = insertWhereClause(countSql, whereClause);

            }
        }


        // Build ORDER BY clause
        if (orderBy != null && !orderBy.isEmpty()) {

            String orderByClause = orderBy.entrySet().stream()
                    .map(e -> {
                        String column = columnMappings.getOrDefault(e.getKey(), e.getKey());
                        return quote(column) + " " + e.getValue();
                    })
                    .collect(Collectors.joining(", ", " ORDER BY ", ""));

            sql += orderByClause;
        }

        sql += " LIMIT :limit OFFSET :offset";

        List<T> rows = new ArrayList<>();

        try {
            rows = namedJdbcTemplate.query(
                    sql,
                    queryParams,
                    mapper
            );
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        Long total = namedJdbcTemplate.queryForObject(
                countSql,
                params,
                Long.class
        );

        return ListResultset.of(
                rows,
                page,
                size,
                total == null ? 0 : total
        );
    }

    @Override
    public <T> T findOneBySql(String sql, Map<String, Object> params, RowMapper<T> mapper) {
try {
    return namedJdbcTemplate.queryForObject(
            sql,
            params,
            mapper
    );
}catch (Exception e){
    System.out.println(sql);
    System.out.println(params);
    System.out.println(e.getMessage());
    System.out.println(e.getCause());
    System.out.println(e.getStackTrace());
    throw e;
}
    }

    @Override
    public void executeSql(String sql, Map<String, Object> params) {
        namedJdbcTemplate.update(sql, params);
    }

    private String insertWhereClause(String sql, String whereClause) {

        if (whereClause == null || whereClause.isBlank()) {
            return sql;
        }

        String lower = sql.toLowerCase();

        int index = Integer.MAX_VALUE;

        int groupBy = lower.indexOf(" group by ");
        if (groupBy >= 0) index = Math.min(index, groupBy);

        int having = lower.indexOf(" having ");
        if (having >= 0) index = Math.min(index, having);

        int orderBy = lower.indexOf(" order by ");
        if (orderBy >= 0) index = Math.min(index, orderBy);

        if (index == Integer.MAX_VALUE) {
            return sql + " WHERE " + whereClause;
        }

        return sql.substring(0, index)
                + " WHERE " + whereClause + " "
                + sql.substring(index);
    }
    private Map<String, Object> normalizeFilters(Map<String, Object> filters) {
        Map<String, Object> normalized = new HashMap<>(filters);

        for (Map.Entry<String, Object> entry : normalized.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Any key starting with "is" should be treated as a boolean
            if (key.startsWith("is")) {
                if (value instanceof String) {
                    String s = ((String) value).trim().toLowerCase();

                    // Convert common truthy/falsey strings
                    if (s.equals("true") || s.equals("1") || s.equals("yes")) {
                        normalized.put(key, Boolean.TRUE);
                    } else if (s.equals("false") || s.equals("0") || s.equals("no")) {
                        normalized.put(key, Boolean.FALSE);
                    } else {
                        // Fallback: Boolean.valueOf handles "true"/"false"
                        normalized.put(key, Boolean.valueOf(s));
                    }
                } else if (value instanceof Number) {
                    normalized.put(key, ((Number) value).intValue() != 0);
                }
            }
        }

        return normalized;
    }

    /**
     * Returns the subset of the given IDs that actually exist in the specialties table.
     * Callers compare this against the full input set to detect any invalid/unknown IDs.
     */
    public Set<UUID> findExistingIds(String table, Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }

        return jdbc.query(
                "select id from " + table + " where id = ANY(?)",
                ps -> {
                    Array sqlArray = ps.getConnection().createArrayOf(
                            "uuid",
                            ids.toArray(new UUID[0])
                    );
                    ps.setArray(1, sqlArray);
                },
                (rs, rowNum) -> (UUID) rs.getObject("id")
        ).stream().collect(Collectors.toSet());
    }


}