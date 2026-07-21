package com.gossamercms.mvc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gossamercms.mvc.models.ModelMeta;
import org.postgresql.util.PGobject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Arrays;

/**
 * Reflection utilities.
 *
 * mapRowToMap() = raw row extraction
 * convertFromDatabase() = type conversion
 * mapRowToDto() = DTO population
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}

    // Cache: DTO class → field name → Field
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    // Cache: DTO class → no-args constructor
    private static final Map<Class<?>, Constructor<?>> CTOR_CACHE = new ConcurrentHashMap<>();

    // ------------------------------------------------------------
    // Extract values from DTO using ModelMeta
    // ------------------------------------------------------------
    public static Map<String, Object> extractValues(Object dto, ModelMeta meta) {
        Map<String, Object> values = new LinkedHashMap<>();

        Map<String, Field> fields = getFields(dto.getClass());

        meta.columns().forEach((name, type) -> {
            Field f = fields.get(name);
            if (f == null) {
                throw new RuntimeException("DTO missing field: " + name);
            }
            try {
                values.put(name, f.get(dto));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to read field: " + name, e);
            }
        });

        return values;
    }

    // ------------------------------------------------------------
    // Map DB row → DTO instance
    // ------------------------------------------------------------
    public static <T> T mapRowToDto(Map<String, Object> row, Class<T> dtoClass) {
        try {
            T dto = newDtoInstance(dtoClass);
            Map<String, Field> fields = getFields(dtoClass);

            for (var entry : row.entrySet()) {
                Field f = fields.get(entry.getKey());

                if (f != null) {
                    Object converted = ReflectionUtils.convertFromDatabase(
                            f.getType(),
                            entry.getValue()
                    );

                    if (converted != null &&
                            !f.getType().isAssignableFrom(converted.getClass())) {

                        System.out.printf(
                                "Field '%s' expects %s but got %s (value=%s)%n",
                                f.getName(),
                                f.getType().getName(),
                                converted.getClass().getName(),
                                converted
                        );
                    }

                    f.set(dto, converted);
                }
            }

            return dto;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to map row to DTO: " + dtoClass.getName(), e);
        }
    }

    public static Object convertForDatabase(
            Class<?> targetType,
            Object value
    ) {
        if (value == null) {
            return null;
        }

        try {

            // Instant -> Timestamp
            if (targetType == Instant.class &&
                    value instanceof Instant instant) {
                return java.sql.Timestamp.from(instant);
            }

            // Map/List -> JSONB
            if ((Map.class.isAssignableFrom(targetType) ||
                    List.class.isAssignableFrom(targetType))
                    && (value instanceof Map || value instanceof List)) {

                PGobject json = new PGobject();
                json.setType("jsonb");
                json.setValue(
                        JsonUtil.OBJECT_MAPPER.writeValueAsString(value)
                );

                return json;
            }

            return value;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed DTO->DB conversion for " +
                            targetType.getName(),
                    e
            );
        }
    }

    public static Object convertFromDatabase(Class<?> targetType, Object value) {
        if (value == null) {
            // primitive boolean default
            return targetType == Boolean.TYPE ? false : null;
        }

        ObjectMapper objectMapper = JsonUtil.OBJECT_MAPPER;

        try {
            // If already correct type, return as-is
            if (targetType.isInstance(value)) {
                return value;
            }

            // ---------- UUID ----------
            if (targetType == UUID.class) {
                if (value instanceof UUID u) return u;
                if (value instanceof String s) return UUID.fromString(s);
            }

            // ---------- Boolean ----------
            if (targetType == Boolean.class || targetType == Boolean.TYPE) {
                if (value instanceof Boolean b) return b;
                if (value instanceof String s) return Boolean.parseBoolean(s);
            }

            // ---------- Instant ----------
            if (targetType == Instant.class) {
                if (value instanceof Timestamp ts) return ts.toInstant();
                if (value instanceof java.sql.Date d) return d.toInstant();
            }

            // ---------- LocalDate ----------
            if (targetType == LocalDate.class) {
                if (value instanceof java.sql.Date d) return d.toLocalDate();
                if (value instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate();
            }

            // ---------- LocalDateTime ----------
            if (targetType == LocalDateTime.class && value instanceof Timestamp ts) {
                return ts.toLocalDateTime();
            }

            // ---------- Timestamp ----------
            if (targetType == Timestamp.class && value instanceof Timestamp ts) {
                return ts.toLocalDateTime();
            }

            // ---------- OffsetDateTime ----------
            if (targetType == OffsetDateTime.class && value instanceof Timestamp ts) {
                return ts.toInstant().atOffset(ZoneOffset.UTC);
            }

            // ---------- Check for Array
            if (value instanceof java.sql.Array sqlArray) {
                Object array = sqlArray.getArray();

                if (targetType == String[].class) {
                    return (String[]) array;
                }

                if (targetType == List.class || List.class.isAssignableFrom(targetType)) {
                    if (array instanceof Object[] objects) {
                        return Arrays.asList(objects);
                    }
                }

                return array;
            }
            // ---------- JSON PGobject → Map ----------
            if (value instanceof PGobject pg) {
                String type = pg.getType();
                if ("json".equalsIgnoreCase(type) || "jsonb".equalsIgnoreCase(type)) {

                    if (Map.class.isAssignableFrom(targetType)) {
                        return objectMapper.readValue(pg.getValue(), Map.class);
                    }

                    if (List.class.isAssignableFrom(targetType)) {
                        return objectMapper.readValue(pg.getValue(), List.class);
                    }

                    if (targetType == String.class) {
                        return pg.getValue();
                    }
                }
            }

            // ---------- Fallback ----------
            return value;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed DB->DTO conversion from " +
                            value.getClass().getName() + " to " +
                            targetType.getName(),
                    e
            );
        }
    }


    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------
    private static Map<String, Field> getFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Map<String, Field> map = new LinkedHashMap<>();
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                map.put(f.getName(), f);
            }
            return map;
        });
    }

    private static <T> T newDtoInstance(Class<T> clazz) {
        try {
            Constructor<?> ctor = CTOR_CACHE.computeIfAbsent(clazz, c -> {
                try {
                    Constructor<?> cons = c.getDeclaredConstructor();
                    cons.setAccessible(true);
                    return cons;
                } catch (Exception e) {
                    throw new RuntimeException("DTO must have a no-args constructor: " + c.getName(), e);
                }
            });

            @SuppressWarnings("unchecked")
            T instance = (T) ctor.newInstance();
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate DTO: " + clazz.getName(), e);
        }
    }

    public static void setIfExists(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException ignored) {
            // Field doesn't exist — skip
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);

        }
    }

    public static Map<String, Object> mapRowToMap(ResultSet rs) {
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }

            return row;

        } catch (Exception e) {
            throw new RuntimeException("Failed to map ResultSet to Map", e);
        }
    }

}