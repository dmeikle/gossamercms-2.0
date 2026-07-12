package com.gossamercms.mvc.handlers;

import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.util.ReflectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseHandler<
        EntityType extends BaseModel,
        DtoType extends DtoWithId
        > {

    protected final BaseDbService<EntityType, DtoType> db;

    protected BaseHandler(BaseDbService<EntityType, DtoType> db) {
        this.db = db;
    }

    // ------------------------------------------------------------
    // GET
    // ------------------------------------------------------------
    public DtoType getById(UUID id) {
        return db.getById(id);
    }

    public DtoType get(Map<String, Object> params) {
        return db.get(params);
    }

    public ListResultset<DtoType> getAll(QueryOptions options) {
        return db.getAll(options);
    }

    // ------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------
    public DtoType create(UUID createdBy, DtoType dto) {

        // Assign ID + timestamps at the handler layer
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        applyFrameworkFields(dto, id, now, now);

        return db.create(createdBy, dto);
    }

    // ------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------
    public DtoType updateById(UUID updatedBy, UUID id, DtoType dto) {

        // Update timestamp
        applyUpdatedAt(dto, Instant.now());

        return db.updateById(updatedBy, dto, id);
    }

    public DtoType update(UUID updatedBy, DtoType dto, Map<String, Object> params) {

        applyUpdatedAt(dto, Instant.now());

        return db.update(updatedBy, dto, params);
    }

    // ------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------
    public void deleteById(UUID deletedBy, UUID id) {
        db.deleteById(deletedBy, id);
    }

    public void delete(UUID deletedBy, Map<String, Object> params) {
        db.delete(deletedBy, params);
    }

    // ------------------------------------------------------------
    // RESTORE
    // ------------------------------------------------------------
    public DtoType restoreById(UUID restoredBy, UUID id) {
        return db.restoreById(restoredBy, id);
    }

    // ------------------------------------------------------------
    // BULK
    // ------------------------------------------------------------
    public ListResultset<DtoType> createOrReplaceBulk(
            UUID deletedBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        return db.createOrReplaceBulk(deletedBy, dtos, deleteExistingKey);
    }

    // ------------------------------------------------------------
    // INTERNAL HELPERS
    // ------------------------------------------------------------
    protected void applyFrameworkFields(DtoType dto, UUID id, Instant createdAt, Instant updatedAt) {
        ReflectionUtils.setIfExists(dto, "id", id);
        ReflectionUtils.setIfExists(dto, "createdAt", createdAt);
        ReflectionUtils.setIfExists(dto, "updatedAt", updatedAt);

        // Support alternate naming conventions
        ReflectionUtils.setIfExists(dto, "createdOn", createdAt);
        ReflectionUtils.setIfExists(dto, "updatedOn", updatedAt);
    }

    protected void applyUpdatedAt(DtoType dto, Instant updatedAt) {
        ReflectionUtils.setIfExists(dto, "updatedAt", updatedAt);
        ReflectionUtils.setIfExists(dto, "updatedOn", updatedAt);
    }
}