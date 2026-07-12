package com.gossamercms.mvc.controllers;

import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;

import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.models.BaseModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseController<
        EntityType extends BaseModel,
        DtoType extends DtoWithId
        > {

    protected final BaseHandler<EntityType, DtoType> handler;

    protected BaseController(BaseHandler<EntityType, DtoType> handler) {
        this.handler = handler;
    }



    // ---------- GET BY ID ----------
    @GetMapping("/{id}")
    public DtoType getById(@PathVariable UUID id) {

        System.out.println("testing get by id " + id);
        return handler.getById(id);
    }

    // ---------- GET BY FILTER ----------
    @GetMapping("/get")
    public DtoType get(@RequestParam Map<String, Object> params) {
        return handler.get(params);
    }

    // ---------- LIST / PAGINATION ----------
    @GetMapping
    public ListResultset<DtoType> getAll(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam Map<String, Object> params
    ) {
        params.remove("page");
        params.remove("size");

        QueryOptions options = QueryOptions.of(page, size, params, Map.of());
        return handler.getAll(options);
    }

    // ---------- CREATE ----------
    @PostMapping
    public DtoType create(
            @RequestHeader("X-User-Id") UUID createdBy,
            @RequestBody DtoType dto
    ) {
        return handler.create(createdBy, dto);
    }

    // ---------- UPDATE BY ID ----------
    @PutMapping("/{id}")
    public DtoType updateById(
            @RequestHeader("X-User-Id") UUID updatedBy,
            @PathVariable UUID id,
            @RequestBody DtoType dto
    ) {
        return handler.updateById(updatedBy, id, dto);
    }

    // ---------- UPDATE BY FILTER ----------
    @PutMapping("/update")
    public DtoType update(
            @RequestHeader("X-User-Id") UUID updatedBy,
            @RequestBody DtoType dto,
            @RequestParam Map<String, Object> params
    ) {
        return handler.update(updatedBy, dto, params);
    }

    // ---------- DELETE BY ID ----------
    @DeleteMapping("/{id}")
    public void deleteById(
            @RequestHeader("X-User-Id") UUID deletedBy,
            @PathVariable UUID id
    ) {
        handler.deleteById(deletedBy, id);
    }

    // ---------- DELETE BY FILTER ----------
    @DeleteMapping
    public void delete(
            @RequestHeader("X-User-Id") UUID deletedBy,
            @RequestParam Map<String, Object> params
    ) {
        handler.delete(deletedBy, params);
    }

    // ---------- RESTORE ----------
    @PostMapping("/{id}/restore")
    public DtoType restore(
            @RequestHeader("X-User-Id") UUID restoredBy,
            @PathVariable UUID id
    ) {
        return handler.restoreById(restoredBy, id);
    }

    // ---------- BULK CREATE OR REPLACE ----------
    @PostMapping("/bulk")
    public ListResultset<DtoType> createOrReplaceBulk(
            @RequestHeader("X-User-Id") UUID deletedBy,
            @RequestBody List<DtoType> dtos,
            @RequestParam Map<String, Object> deleteExistingKey
    ) {
        return handler.createOrReplaceBulk(deletedBy, dtos, deleteExistingKey);
    }

    protected Map<String, String> getOrderBy(Map<String, Object> params) {

        Object sort = params.remove("sort");
        Object dir = params.remove("dir");

        params.remove("page");
        params.remove("size");

        Map<String, String> orderBy = Map.of();

        if (sort != null) {
            orderBy = Map.of(
                    sort.toString(),
                    dir == null ? "ASC" : dir.toString().toUpperCase()
            );
        }
        return orderBy;
    }

}