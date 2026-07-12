package com.gossamercms.auth.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.*;
import com.gossamercms.auth.dtos.PermissionDto;
import com.gossamercms.auth.models.Permission;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class PermissionsDbService extends BaseDbService<Permission, PermissionDto> {

    public PermissionsDbService(DataSourceManager dsManager) {
        super(Permission.class, PermissionDto.class, dsManager);
    }

    @Override
    protected Permission mapToEntity(PermissionDto dto) {
        return Permission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    protected PermissionDto mapToDto(Permission entity) {
        return PermissionDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected PermissionDto removeExcludedFields(PermissionDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Permission not found: " + id);
    }

    @Override
    public ListResultset<PermissionDto> createOrReplaceBulk(UUID deletedBy, List<PermissionDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk permission creation not supported.");
    }

    public ListResultset<PermissionDto> getAllPermissionsByUserContextId(UUID userContextId) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        String sql = """
            select p.*
            from permissions p
            inner join role_permissions rp
                on rp."permissionId" = p.id
            inner join user_contexts uc
                on uc."roleId" = rp."roleId"
           
            """;

        String countSql = """
            select count(*)
            from permissions p
            inner join role_permissions rp
                on rp."permissionId" = p.id
            inner join user_contexts uc
                on uc."roleId" = rp."roleId"
           
    """;

        Map<String, Object> params = Map.of(
                "userContextId", userContextId
        );


        ListResultset<PermissionDto> result =
                ds.findAllBySql(
                        sql,
                        countSql,
                        params,
                        Map.of(
                                "userContextId", "uc.id"
                        ),
                        Collections.emptyMap(),
                        1,
                        100,
                        (rs, rowNum) -> PermissionDto.builder()
                                .id(rs.getObject("id", UUID.class))
                                .name(rs.getString("name"))
                                .build()
                );
        return result;
    }


    public ListResultset<PermissionDto> getAllPermissionsByRoleId(UUID roleId) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        String sql = """
            select p.*
            from permissions p
            inner join role_permissions rp
                on rp."permissionId" = p.id
           """;

        String countSql = """
            select count(*)
            from permissions p
            inner join role_permissions rp
                on rp."permissionId" = p.id
    """;

        Map<String, Object> params = Map.of(
                "roleId", roleId
        );


        ListResultset<PermissionDto> result =
                ds.findAllBySql(
                        sql,
                        countSql,
                        params,
                        Map.of(
                                "roleId", "rp.\"roleId\""
                        ),
                        Collections.emptyMap(),
                        1,
                        100,
                        (rs, rowNum) -> PermissionDto.builder()
                                .id(rs.getObject("id", UUID.class))
                                .name(rs.getString("name"))
                                .build()
                );
        return result;
    }
}