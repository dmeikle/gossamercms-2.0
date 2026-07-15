package com.gossamercms.mvc.controllers;

import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.mvc.handlers.BaseHandler;

import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
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
    public ApiResponse<DtoType> getById(@PathVariable UUID id) {

        System.out.println("testing get by id " + id);
        return ApiResponse.ok(handler.getById(id));
    }

    // ---------- GET BY FILTER ----------
    @GetMapping("/get")
    public ApiResponse<DtoType> get(@RequestParam Map<String, Object> params) {
        return ApiResponse.ok(handler.get(params));
    }

    // ---------- LIST / PAGINATION ----------
    @GetMapping
    public ApiResponse<ListResultset<DtoType>> getAll(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam Map<String, Object> params
    ) {
        params.remove("page");
        params.remove("size");

        QueryOptions options = QueryOptions.of(page, size, params, Map.of());
        return ApiResponse.ok(handler.getAll(options));
    }

    // ---------- CREATE ----------
    @PostMapping
    public ApiResponse<DtoType> create(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody DtoType dto
    ) {
        if(!jwtUser.isAuthenticated()) {
            throw new UnauthorizedStatusException();
        }

        return ApiResponse.ok(handler.create(jwtUser.getUserId(), dto));
    }

    // ---------- UPDATE BY ID ----------
    @PutMapping("/{id}")
    public ApiResponse<DtoType> updateById(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable UUID id,
            @RequestBody DtoType dto
    ) {
        return ApiResponse.ok(handler.updateById(jwtUser.getUserId(), id, dto));
    }

    // ---------- UPDATE BY FILTER ----------
    @PutMapping("/update")
    public ApiResponse<DtoType> update(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody DtoType dto,
            @RequestParam Map<String, Object> params
    ) {
        return ApiResponse.ok(handler.update(jwtUser.getUserId(), dto, params));
    }

    // ---------- DELETE BY ID ----------
    @DeleteMapping("/{id}")
    public void deleteById(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable UUID id
    ) {
        handler.deleteById(jwtUser.getUserId(), id);
    }

    // ---------- DELETE BY FILTER ----------
    @DeleteMapping
    public void delete(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestParam Map<String, Object> params
    ) {
        handler.delete(jwtUser.getUserId(), params);
    }

    // ---------- RESTORE ----------
    @PostMapping("/{id}/restore")
    public ApiResponse<DtoType> restore(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable UUID id
    ) {
        return ApiResponse.ok(handler.restoreById(jwtUser.getUserId(), id));
    }

    // ---------- BULK CREATE OR REPLACE ----------
    @PostMapping("/bulk")
    public ApiResponse<ListResultset<DtoType>> createOrReplaceBulk(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody List<DtoType> dtos,
            @RequestParam Map<String, Object> deleteExistingKey
    ) {
        return ApiResponse.ok(handler.createOrReplaceBulk(jwtUser.getUserId(), dtos, deleteExistingKey));
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