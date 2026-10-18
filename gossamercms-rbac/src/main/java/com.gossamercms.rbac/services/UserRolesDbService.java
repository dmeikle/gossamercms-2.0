package com.gossamercms.rbac.services;

import com.gossamercms.mvc.data.*;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.dtos.UserRoleDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserRolesDbService extends BaseDbService<UserRole, UserRoleDto> {

    public UserRolesDbService(DataSourceManager dsManager) {
        super(UserRole.class, UserRoleDto.class, dsManager);
    }

    @Override
    protected UserRole mapToEntity(UserRoleDto dto) {
        return dto.toEntity();
    }

    @Override
    protected UserRoleDto mapToDto(UserRole entity) {
        return entity.toDto();
    }

    @Override
    protected UserRoleDto removeExcludedFields(UserRoleDto dto) {
        return dto;
    }

//    @Override
//    protected void throw404(String id) {
//        throw new RuntimeException("UserRole not found: " + id);
//    }

    @Override
    public ListResultset<UserRoleDto> createOrReplaceBulk(UUID deletedBy, List<UserRoleDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk user-role creation not supported.");
    }

    public ListResultset<RoleDto> getRolesByUserContextId(QueryOptions options) {

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        String sql = """
                select r.* from roles r
                    inner join user_roles ur on ur."roleId" = r.id
                    /*WHERE_CLAUSE*/
                """;
        String countSql = """
                select count(r.id) from roles r
                    inner join user_roles ur on ur."roleId" = r.id
                    /*WHERE_CLAUSE*/
                """;
        return ds.findAllBySql(
                sql,
                countSql,
                options.filters(),
                Map.of(
                        "userContextId", "ur.userContextId"
                ),
                Collections.emptyMap(),
                options.page(),
                options.size(),
                (rs, rowNum) -> RoleDto.builder()
                            .id(rs.getObject("id", UUID.class))
                            .description(rs.getString("description"))
                            .name(rs.getString("name"))
                            .isSystem(rs.getBoolean("isSystem"))
                            .build()

        );
    }
}