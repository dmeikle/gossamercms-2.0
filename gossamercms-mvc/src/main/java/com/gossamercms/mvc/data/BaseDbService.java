package com.gossamercms.mvc.data;

import com.gossamercms.mvc.exceptions.ApiException;
import com.gossamercms.mvc.exceptions.NotFoundException;
import com.gossamercms.mvc.helpers.JsonbHelper;
import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.mvc.util.ReflectionUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseDbService<
        EntityType extends BaseModel,
        DtoType extends DtoWithId
        > {

    protected final DataSourceManager dsManager;
    protected final ModelMeta meta;
    protected final Class<DtoType> dtoClass;
    // Cached SQL string generated once per service lifecycle
    private final String batchUpdateSql;

    protected BaseDbService(
            Class<EntityType> modelClass,
            Class<DtoType> dtoClass,
            DataSourceManager dsManager
    ) {
        this.meta = BaseModel.metaOf(modelClass);
        this.dtoClass = dtoClass;
        this.dsManager = dsManager;
        this.batchUpdateSql = buildBatchUpdateSql(this.meta);
    }

    // ---------- Mapping ----------
    protected abstract EntityType mapToEntity(DtoType dto);
    protected abstract DtoType mapToDto(EntityType entity);
    /**
     * Constructs:
     * UPDATE users SET firstname = :firstname, lastname = :lastname, status = :status ... WHERE id = :id
     */
    private String buildBatchUpdateSql(ModelMeta meta) {
        // Exclude 'id' and read-only columns like 'createdOn' from the SET clause
        Set<String> excludedColumns = Set.of("id", "createdon", "created_on");

        String setAssignments = meta.columns().keySet().stream()
                .filter(columnName -> !excludedColumns.contains(columnName.toLowerCase()))
                .map(columnName -> String.format("%s = :%s", columnName, columnName))
                .collect(Collectors.joining(", "));

        return String.format(
                "UPDATE %s SET %s WHERE id = :id",
                meta.table(),
                setAssignments
        );
    }
    protected <OverrideDto extends DtoType> OverrideDto mapToDto(
            EntityType entity,
            Class<OverrideDto> overrideClass
    ) {
        try {
            return overrideClass.getConstructor(entity.getClass()).newInstance(entity);
        } catch (Exception e) {
            throw new RuntimeException("DTO mapping failed", e);
        }
    }

    // ---------- Filtering ----------
    protected Map<String, Object> createFilter(Map<String, Object> params) {
        Map<String, Object> validated = new LinkedHashMap<>();

        for (var entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Class<?> expectedType = meta.columns().get(key);
            if (expectedType == null) {
                throw new IllegalArgumentException("Unknown column: " + key);
            }

            if (value == null) {
                validated.put(key, null);
                continue;
            }

            validated.put(key, convertFilterValue(expectedType, value));
        }

        return validated;
    }

    private Object convertFilterValue(Class<?> expectedType, Object value) {

        if (expectedType.isInstance(value)) {
            return value;
        }

        if (expectedType == UUID.class) {
            if (value instanceof String s) {
                return UUID.fromString(s);
            }
            return value;
        }

        if (expectedType == Instant.class && value instanceof Timestamp ts) {
            return ts.toInstant();
        }

        if ((expectedType == Boolean.class || expectedType == boolean.class)
                && value instanceof String s) {
            return Boolean.parseBoolean(s);
        }

        return value;
    }

    // ---------- Ordering ----------
    protected Map<String, String> getOrderBy() {
        return Map.of("id", "asc");
    }

    protected Map<String, String> getOrderByCustom() {
        return Map.of();
    }

    // ---------- Exclusions ----------
    protected List<String> getExcludedFields() {
        return List.of();
    }

    protected abstract DtoType removeExcludedFields(DtoType dto);

    // ---------- CRUD ----------
    public DtoType create(UUID createdBy, DtoType dto) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        EntityType entity = mapToEntity(dto);

        // Always generate a new ID
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, UUID.randomUUID());
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to auto-generate ID", e);
        }

        // createdOn
        try {
            Field createdOn = entity.getClass().getDeclaredField("createdOn");
            createdOn.setAccessible(true);
            if (createdOn.get(entity) == null) {
                createdOn.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set createdOn", e);
        }

        // createdAt
        try {
            Field createdAt = entity.getClass().getDeclaredField("createdAt");
            createdAt.setAccessible(true);
            if (createdAt.get(entity) == null) {
                createdAt.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set createdAt", e);
        }

        // Build params (JSONB included)
        Map<String, Object> params = buildParamsForSave(entity);
        try {
            // Save using params
            ds.save(meta.table(), params);
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return mapToDto(entity);
    }

    protected Map<String, Object> buildParamsForSave(EntityType entity) {
        Map<String, Object> params = new HashMap<>();

        try {
            // DB column names → Java field names must match
            Map<String, Class<?>> columns = meta.columns();

            for (String columnName : columns.keySet()) {

                // Java field must have the SAME NAME as the DB column
                Field field = entity.getClass().getDeclaredField(columnName);
                field.setAccessible(true);

                Object value = field.get(entity);

                // JSONB conversion
                if (field.isAnnotationPresent(JsonColumn.class)) {
                    params.put(columnName, JsonbHelper.toJsonb(value));
                } else {
                    params.put(columnName, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to build params for save", e);
        }

        return params;
    }




    public DtoType getById(UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = Map.of("id", id);

        Map<String, Object> row = (Map) ds.findOne(meta.table(), filter);
        if (row == null) {
            throw404("id=" + id);
        }

        DtoType dto = ReflectionUtils.mapRowToDto(row, dtoClass);
        return removeExcludedFields(dto);
    }

    public DtoType get(Map<String, Object> params) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = createFilter(params);
        @SuppressWarnings("unchecked")
        Map<String, Object> row = (Map<String, Object>) ds.findOne(meta.table(), filter);

        if (row == null) {
            throw404(filter.toString());
        }

        DtoType dto = ReflectionUtils.mapRowToDto(row, dtoClass);
        return removeExcludedFields(dto);
    }

    public DtoType getOrNull(Map<String, Object> params) {
        try {
            return get(params);
        } catch (Exception e) {
            return null;
        }
    }

    public DtoType updateById(UUID updatedBy, DtoType dto, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        EntityType entity = mapToEntity(dto);

        Map<String, Object> params = buildParamsForSave(entity);

        @SuppressWarnings("unchecked")
        Map<String, Object> row = (Map<String, Object>) ds.update(meta.table(), id, params);

        DtoType updatedDto = ReflectionUtils.mapRowToDto(row, dtoClass);

        return removeExcludedFields(updatedDto);
    }

    public DtoType update(UUID updatedBy, DtoType dto, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType existing = get(filter);
        UUID id = existing.getId();

        return updateById(updatedBy, dto, id);
    }

    public ListResultset<DtoType> getAll(QueryOptions options) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = createFilter(options.filters());
        Map<String, String> orderBy = validateOrderBy(options.orderBy());

        ListResultset<?> raw = ds.findMany(
                meta,
                options
        );

        List<Map<String, Object>> rows = (List<Map<String, Object>>) raw.list();

        List<DtoType> dtos = rows.stream()
                .map(r -> ReflectionUtils.mapRowToDto(r, dtoClass))
                .toList();

        return new ListResultset<>(
                dtos,
                raw.page(),
                raw.pageSize(),
                raw.totalCount(),
                raw.totalPages()
        );
    }

    protected Map<String, String> validateOrderBy(Map<String, String> orderBy) {
        Map<String, String> validated = new LinkedHashMap<>();

        for (var entry : orderBy.entrySet()) {
            if (!meta.columns().containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Unknown column in orderBy: " + entry.getKey());
            }

            String direction = entry.getValue().toLowerCase();
            if (!direction.equals("asc") && !direction.equals("desc")) {
                throw new IllegalArgumentException("Invalid sort direction: " + entry.getValue());
            }

            validated.put(entry.getKey(), direction);
        }

        return validated;
    }


  //  @Transactional
    public ListResultset<DtoType> createOrReplaceBulk(
            UUID createdBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        // Soft-delete everything currently matching the key
        if (!deleteExistingKey.isEmpty()) {
            String where = deleteExistingKey.keySet().stream()
                    .map(k -> "\"" + k + "\" = :" + k)
                    .collect(Collectors.joining(" AND "));

            Map<String, Object> params = new HashMap<>(deleteExistingKey);
            params.put("deletedBy", createdBy);

            String sql = """
            UPDATE "%s"
            SET "deletedAt" = now(),
                "deletedBy" = :deletedBy
            WHERE %s
            AND "deletedAt" IS NULL
            """.formatted(meta.table(), where);

            ds.executeSql(sql, params);
        }

        // Create each incoming dto fresh, reusing existing create() logic
        List<DtoType> saved = new ArrayList<>();
        for (DtoType dto : dtos) {
            saved.add(create(createdBy, dto));
        }

        return ListResultset.of(saved, 1, -1, saved.size());
    }

    public void deleteById(UUID deletedBy, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        if (!ds.exists(meta.table(), id)) {
            throw404("Record not found: " + id);
        }

        ds.delete(meta.table(), id);
    }

    public void delete(UUID deletedBy, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType dto = get(filter);
        UUID id = dto.getId();

        deleteById(deletedBy, id);
    }

    public DtoType restoreById(UUID restoredBy, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Object restoredEntity = ds.restoreById(meta.table(), id, restoredBy);

        if (restoredEntity == null) {
            throw404("Record not found or not deleted: " + id);
        }

        return removeExcludedFields(mapToDto((EntityType) restoredEntity));
    }

    public DtoType restore(UUID restoredBy, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType deletedRecord = getIncludingDeleted(filter);
        UUID id = deletedRecord.getId();

        return restoreById(restoredBy, id);
    }

    protected DtoType getIncludingDeleted(Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Object entity = ds.findOneIncludingDeleted(meta.table(), filter);

        if (entity == null) {
            throw404("Record not found (including deleted): " + filter);
        }

        return removeExcludedFields(mapToDto((EntityType) entity));
    }

    /**
     * we don't always want to throw a 404
     *
     * @param params
     * @return
     */
    public DtoType optionalGet(Map<String, Object> params) {
        try {
            return get(params);
        } catch (ApiException ex) {
            if (ex.getStatus() == 404) {
                return null;
            }
            throw ex;
        }
    }

    protected  void throw404(String id) {
        throw  new NotFoundException(id);
    }

    protected Instant toInstant(Object value) {
        if (value == null) return null;
        if (value instanceof Instant i) return i;
        if (value instanceof Timestamp ts) return ts.toInstant();
        throw new IllegalArgumentException("Unsupported timestamp type: " + value.getClass());
    }

    protected boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    protected Map<String, Object> normalizeFilters(Map<String, Object> filters) {
        Map<String, Object> normalized = new HashMap<>(filters);

        for (Map.Entry<String, Object> entry : normalized.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith("is")) {
                if (value instanceof String s) {
                    s = s.trim().toLowerCase();
                    if (s.equals("true") || s.equals("1") || s.equals("yes")) {
                        normalized.put(key, Boolean.TRUE);
                    } else if (s.equals("false") || s.equals("0") || s.equals("no")) {
                        normalized.put(key, Boolean.FALSE);
                    } else {
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
     * Efficiently updates a batch of DTOs in a single JDBC batch operation.
     */
    @Transactional
    public List<DtoType> updateAll(UUID updatedBy, List<DtoType> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        NamedParameterJdbcTemplate jdbcTemplate = ds.getNamedParameterJdbcTemplate();

        // Map DTO properties directly to SQL named parameters matching column names
        SqlParameterSource[] batchParams = dtos.stream()
                .map(dto -> {
                    BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(dto);
                    // Handle system audit metadata dynamically
                    paramSource.registerSqlType("updatedBy", java.sql.Types.OTHER);
                    return paramSource;
                })
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(batchUpdateSql, batchParams);

        return dtos;
    }
}
