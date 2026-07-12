package com.gossamercms.mvc.factories;

import com.gossamercms.mvc.data.DtoWithId;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

public final class DtoFactory {

    public static <T extends DtoWithId> T withNewId(T dto, UUID createdBy) {
        try {
            // Create new instance
            @SuppressWarnings("unchecked")
            T copy = (T) dto.getClass().getDeclaredConstructor().newInstance();

            // Copy all fields from original DTO
            for (Field field : dto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(dto);
                field.set(copy, value);
            }

            // Apply ID
            setIfExists(copy, "id", UUID.randomUUID());

            // Apply timestamps (only if fields exist)
            Instant now = Instant.now();
            setIfExists(copy, "createdAt", now);
            setIfExists(copy, "updatedAt", now);
            setIfExists(copy, "createdOn", now);  // supports your UserDto
            setIfExists(copy, "updatedOn", now);

            return copy;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create DTO copy", e);
        }
    }

    private static void setIfExists(Object target, String fieldName, Object value) {
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
}