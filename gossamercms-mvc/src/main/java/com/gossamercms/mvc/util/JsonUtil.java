package com.gossamercms.mvc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class JsonUtil {

    public static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();

    private JsonUtil() {}

    public static <T> T toObject(ResultSet rs,
                                 String column,
                                 Class<T> type)
            throws SQLException {

        String json = rs.getString(column);

        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new SQLException(
                    "Unable to deserialize JSON column '" + column + "'", e);
        }
    }

    public static <T> List<T> toList(ResultSet rs,
                                     String column,
                                     TypeReference<List<T>> type)
            throws SQLException {

        String json = rs.getString(column);

        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new SQLException(
                    "Unable to deserialize JSON column '" + column + "'", e);
        }
    }

    public static <T> List<T> toList(ResultSet rs,
                                     String column,
                                     Class<T> elementType)
            throws SQLException {
try {
    String json = rs.getString(column);

    if (json == null || json.isBlank()) {
        return Collections.emptyList();
    }

    try {
        return OBJECT_MAPPER.readValue(
                json,
                OBJECT_MAPPER.getTypeFactory()
                        .constructCollectionType(List.class, elementType));
    } catch (JsonProcessingException e) {
        throw new SQLException(
                "Unable to deserialize JSON column '" + column + "'", e);
    }
}catch (Exception e) {
    e.printStackTrace();
    throw e;
}
    }
}