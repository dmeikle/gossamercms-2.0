package com.gossamercms.mvc.data;

import com.gossamercms.mvc.models.ModelMeta;

import org.springframework.jdbc.core.RowMapper;
import java.util.Map;
import java.util.UUID;

public interface DataSourceAdapter {
    String key();
    void save(String table, Map<String, Object> params);

    Object find(String id);
    Object findOne(String table, Map<String,Object> filter);
    Object findOneIncludingDeleted(
            String table,
            Map<String, Object> filter
    );
    //Object update(String table, UUID id, Object dto);
    Map<String, Object> update(String table, UUID id, Map<String, Object> params);

    void delete(String table, UUID id);

    Object restoreById(String table, UUID id, UUID restoredBy);

    long count(String table, Map<String,Object> filter);

    ListResultset<?> findMany(
            ModelMeta meta,
            Map<String,Object> filter,
            Map<String,String> orderBy,
            int page,
            int size
    );

    /**
     * this should only be offered for SQL based operations
     *
     * @param table
     * @param id
     * @return
     */
    default boolean exists(String table, UUID id) {
        throw new UnsupportedOperationException("exists() not supported");
    }


    <T> ListResultset<T> findAllBySql(
            String sql,
            String countSql,
            Map<String, Object> params,
            Map<String, String> columnMappings,
            Map<String, String> orderBy,
            int page,
            int size,
            RowMapper<T> mapper
    );

    public <T> T findOneBySql(String sql, Map<String, Object> params, RowMapper<T> mapper);

    public String buildWhere(
            Map<String, Object> filters,
            Map<String, String> columnMappings
    );
}