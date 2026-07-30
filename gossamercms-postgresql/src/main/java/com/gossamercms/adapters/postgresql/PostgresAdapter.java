package com.gossamercms.adapters.postgresql;


import com.gossamercms.mvc.data.DataSourceAdapter;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryFilter;
import com.gossamercms.mvc.data.QueryOptions;
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
    public Object findOne(
            String table,
            Map<String, Object> filter
    ) {
        return findOne(
                table,
                filter,
                List.of(),
                Collections.emptyMap()
        );
    }


    public Object findOne(
            String table,
            Map<String, Object> filter,
            Map<String, String> columnMappings
    ) {
        return findOne(
                table,
                filter,
                List.of(),
                columnMappings
        );
    }


    public Object findOne(
            String table,
            Map<String, Object> filter,
            List<QueryFilter> queryFilters,
            Map<String, String> columnMappings
    ) {

        Map<String,Object> params = new HashMap<>();

        if (filter != null) {
            params.putAll(filter);
        }


        if (queryFilters != null) {

            for (QueryFilter queryFilter : queryFilters) {

                params.put(
                        queryFilter.field(),
                        queryFilter.value()
                );
            }
        }


        String where =
                buildWhere(
                        filter,
                        queryFilters,
                        columnMappings,
                        false
                );


        String sql =
                "SELECT * FROM " + quote(table);


        if (where != null && !where.isBlank()) {
            sql += " WHERE " + where;
        }


        sql += " LIMIT 1";


        List<Map<String,Object>> rows =
                namedJdbcTemplate.queryForList(
                        sql,
                        params
                );


        return rows.isEmpty()
                ? null
                : rows.getFirst();
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
    public long count(
            String table,
            Map<String, Object> filter,
            List<QueryFilter> queryFilters
    ) {

        Map<String,Object> params = new HashMap<>();

        if (filter != null) {
            params.putAll(filter);
        }


        if (queryFilters != null) {

            for (QueryFilter queryFilter : queryFilters) {

                params.put(
                        queryFilter.field(),
                        queryFilter.value()
                );

            }
        }


        String where =
                buildWhere(
                        filter,
                        queryFilters,
                        Collections.emptyMap(),
                        false
                );


        String sql =
                "SELECT COUNT(*) FROM " + quote(table);


        if (where != null && !where.isBlank()) {
            sql += " WHERE " + where;
        }


        return namedJdbcTemplate.queryForObject(
                sql,
                params,
                Long.class
        );
    }

    // ------------------------------------------------------------
    // FIND MANY
    // ------------------------------------------------------------
    @Override
    public ListResultset<?> findMany(
            ModelMeta meta,
            QueryOptions options
    ) {

        int page = Math.max(1, options.page());
        int size = Math.max(1, options.size());

        int offset = (page - 1) * size;


        Map<String,Object> normalizedFilter =
                normalizeFilters(options.filters());


        boolean softDeletable =
                meta.columns().containsKey("deletedAt");


        String where =
                buildWhere(
                        normalizedFilter,
                        options.queryFilters(),
                        Collections.emptyMap(),
                        softDeletable
                );


        String sql =
                "SELECT * FROM " + meta.table();


        if (!where.isBlank()) {
            sql += " WHERE " + where;
        }


        if (options.orderBy() != null
                && !options.orderBy().isEmpty()) {

            sql += " ORDER BY " +
                    options.orderBy()
                            .entrySet()
                            .stream()
                            .map(e ->
                                    quote(e.getKey())
                                            + " "
                                            + e.getValue()
                            )
                            .collect(Collectors.joining(", "));
        }


        sql += " LIMIT :limit OFFSET :offset";


        Map<String, Object> params =
                new HashMap<>(normalizedFilter);

        if (options.queryFilters() != null) {
            for (QueryFilter queryFilter : options.queryFilters()) {
                String parameterName = queryFilter.field();
                params.put(parameterName, queryFilter.value());
            }
        }

        params.put(
                "limit",
                size
        );

        params.put(
                "offset",
                offset
        );


        List<Map<String,Object>> rows =
                namedJdbcTemplate.queryForList(
                        sql,
                        params
                );


        long total =
                count(
                        meta.table(),
                        normalizedFilter,
                        options.queryFilters()
                );


        return ListResultset.of(
                rows,
                page,
                size,
                total
        );
    }

    @Override
    public boolean exists(String table, UUID id) {
        return DataSourceAdapter.super.exists(table, id);
    }

    // ------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------

    /**
     columnMappings - Map an external filter name to the SQL expression that should be filtered.

     For example:

     filters:
     {
     "providerId": uuid,
     "createdAtStart": ...,
     "createdAtEnd": ...
     }

     columnMappings:
     {
     "providerId" -> "e.provider_id",
     "createdAt" -> "e.created_at"
     }
     *
     *
     * @param filters
     * @param columnMappings
     * @param excludeSoftDeleted
     * @return
     */
    public String buildWhere(
            Map<String,Object> filters,
            List<QueryFilter> queryFilters,
            Map<String,String> columnMappings,
            boolean excludeSoftDeleted
    ) {

        List<String> clauses = new ArrayList<>();


        if (excludeSoftDeleted) {

            clauses.add(
                    "\"deletedAt\" IS NULL"
            );
        }


        /*
         * Existing map filters
         */
        if (filters != null && !filters.isEmpty()) {

            for (Map.Entry<String,Object> entry : filters.entrySet()) {

                String key = entry.getKey();
                Object value = entry.getValue();


                if (value == null) {
                    continue;
                }


                if (isIgnoredFilter(key)) {
                    continue;
                }


                String base =
                        stripOperator(
                                stripRangeSuffix(key)
                        );


                String mapped =
                        columnMappings.getOrDefault(
                                base,
                                base
                        );


                String column =
                        buildColumnExpression(mapped);


                String operator = "=";


                if (key.endsWith("__like")) {
                    operator = "LIKE";
                }
                else if (key.endsWith("__ilike")) {
                    operator = "ILIKE";
                }


                clauses.add(
                        column
                                + " "
                                + operator
                                + " :"
                                + key
                );
            }
        }



        /*
         * QueryFilter support
         */
        if (queryFilters != null) {

            for (QueryFilter filter : queryFilters) {


                String mapped =
                        columnMappings.getOrDefault(
                                filter.field(),
                                filter.field()
                        );


                String column =
                        buildColumnExpression(mapped);


                String parameter =
                        filter.field();


                switch(filter.operator()) {

                    case EQUALS ->
                            clauses.add(
                                    column + " = :" + parameter
                            );


                    case LIKE ->
                            clauses.add(
                                    column + " LIKE :" + parameter
                            );


                    case ILIKE ->
                            clauses.add(
                                    column + " ILIKE :" + parameter
                            );


                    case GREATER_THAN ->
                            clauses.add(
                                    column + " > :" + parameter
                            );


                    case GREATER_THAN_OR_EQUAL ->
                            clauses.add(
                                    column + " >= :" + parameter
                            );


                    case LESS_THAN ->
                            clauses.add(
                                    column + " < :" + parameter
                            );


                    case LESS_THAN_OR_EQUAL ->
                            clauses.add(
                                    column + " <= :" + parameter
                            );


                    case IN ->
                            clauses.add(
                                    column + " IN (:" + parameter + ")"
                            );


                    case NOT_IN ->
                            clauses.add(
                                    column + " NOT IN (:" + parameter + ")"
                            );


                    case IS_NULL ->
                            clauses.add(
                                    column + " IS NULL"
                            );


                    case IS_NOT_NULL ->
                            clauses.add(
                                    column + " IS NOT NULL"
                            );
                }
            }
        }


        return String.join(
                " AND ",
                clauses
        );
    }

    private boolean isIgnoredFilter(String key) {

        return key.equals("page")
                || key.equals("size")
                || key.equals("sortBy")
                || key.equals("direction");

    }


    private String stripOperator(String key) {

        if (key.endsWith("__like")) {

            return key.substring(
                    0,
                    key.length()-6
            );

        }


        if (key.endsWith("__ilike")) {

            return key.substring(
                    0,
                    key.length()-7
            );

        }


        return key;
    }



    private String stripRangeSuffix(String key) {

        if (key.endsWith("Start")) {

            return key.substring(
                    0,
                    key.length()-5
            );

        }


        if (key.endsWith("End")) {

            return key.substring(
                    0,
                    key.length()-3
            );

        }


        return key;
    }



    private String buildColumnExpression(String mapped) {

        if (mapped.contains("::")) {

            String[] parts =
                    mapped.split("::",2);


            return quote(parts[0])
                    + "::"
                    + parts[1];

        }


        return quote(mapped);

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
            Map<String,Object> params,
            Map<String,String> columnMappings,
            Map<String,String> orderBy,
            int page,
            int size,
            RowMapper<T> mapper
    ) {

        return findAllBySql(
                sql,
                countSql,
                params,
                List.of(),
                columnMappings,
                orderBy,
                page,
                size,
                mapper
        );
    }

    public <T> ListResultset<T> findAllBySql(
            String sql,
            String countSql,
            Map<String, Object> params,
            List<QueryFilter> queryFilters,
            Map<String, String> columnMappings,
            Map<String, String> orderBy,
            int page,
            int size,
            RowMapper<T> mapper
    ) {

        page = Math.max(1, page);
        size = Math.max(1, size);

        int offset = (page - 1) * size;


        Map<String,Object> queryParams =
                new HashMap<>();


        if (params != null) {
            queryParams.putAll(params);
        }


        if (queryFilters != null) {

            for (QueryFilter filter : queryFilters) {

                queryParams.put(
                        filter.field(),
                        filter.value()
                );
            }
        }


        queryParams.put("limit", size);
        queryParams.put("offset", offset);


        String whereClause =
                buildWhere(
                        params,
                        queryFilters,
                        columnMappings,
                        false
                );


        if (!whereClause.isBlank()) {

            if (sql.contains("/*WHERE_CLAUSE*/")) {

                sql = sql.replace(
                        "/*WHERE_CLAUSE*/",
                        " WHERE " + whereClause
                );

                countSql = countSql.replace(
                        "/*WHERE_CLAUSE*/",
                        " WHERE " + whereClause
                );

            }
            else if (hasWhereClause(sql)) {

                sql += " AND " + whereClause;
                countSql += " AND " + whereClause;

            }
            else {

                sql = insertWhereClause(
                        sql,
                        whereClause
                );

                countSql = insertWhereClause(
                        countSql,
                        whereClause
                );
            }
        }


        if (orderBy != null && !orderBy.isEmpty()) {

            String orderByClause =
                    orderBy.entrySet()
                            .stream()
                            .map(e -> {

                                String column =
                                        columnMappings.getOrDefault(
                                                e.getKey(),
                                                e.getKey()
                                        );

                                return quote(column)
                                        + " "
                                        + e.getValue();

                            })
                            .collect(Collectors.joining(
                                    ", ",
                                    " ORDER BY ",
                                    ""
                            ));

            sql += orderByClause;
        }


        sql += " LIMIT :limit OFFSET :offset";


        List<T> rows =
                namedJdbcTemplate.query(
                        sql,
                        queryParams,
                        mapper
                );


        Long total =
                namedJdbcTemplate.queryForObject(
                        countSql,
                        queryParams,
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
    private Map<String,Object> normalizeFilters(
            Map<String,Object> filters
    ) {

        if (filters == null || filters.isEmpty()) {
            return Map.of();
        }


        Map<String,Object> normalized =
                new HashMap<>();


        filters.forEach((key,value)->{


            if (value == null) {

                normalized.put(
                        key,
                        null
                );

                return;

            }



            // LIKE values

            if (key.endsWith("__like")
                    || key.endsWith("__ilike")) {


                if (value instanceof String s) {

                    normalized.put(
                            key,
                            "%" + s.trim() + "%"
                    );

                    return;
                }

            }



            // Boolean fields only:
            // isActive
            // isDeleted
            // isSigned

            if (isBooleanField(key)) {

                normalized.put(
                        key,
                        convertBoolean(value)
                );

                return;
            }



            normalized.put(
                    key,
                    value
            );

        });


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
    private boolean isBooleanField(String key) {

        return key.startsWith("is")
                && key.length() > 2
                && Character.isUpperCase(
                key.charAt(2)
        );

    }



    private Boolean convertBoolean(Object value) {

        if (value instanceof Boolean b) {
            return b;
        }


        if (value instanceof Number n) {

            return n.intValue() != 0;

        }


        if (value instanceof String s) {

            s = s.trim().toLowerCase();


            return switch(s) {

                case "true",
                     "1",
                     "yes" ->
                        true;

                case "false",
                     "0",
                     "no" ->
                        false;

                default ->
                        Boolean.valueOf(s);

            };

        }


        return false;
    }

}