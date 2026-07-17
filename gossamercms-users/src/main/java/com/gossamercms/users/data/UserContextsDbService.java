package com.gossamercms.users.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.*;
import com.gossamercms.users.api.UserContextDetailDto;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDirectoryDto;
import com.gossamercms.users.domain.UserContext;
import com.gossamercms.users.policies.UserDirectoryQueryPolicy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ModuleDbService
public class UserContextsDbService extends BaseDbService<UserContext, UserContextDto> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public UserContextsDbService(DataSourceManager dsManager) {
        super(UserContext.class, UserContextDto.class, dsManager);
        System.out.println("************************* UserContextsDbService constructed *********************");
    }

    @Override
    protected UserContextDto mapToDto(UserContext entity) {
        if (entity == null) return null;

        return UserContextDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .contextType(entity.getContextType())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .roleId(entity.getRoleId())
                .build();
    }

    @Override
    protected UserContext mapToEntity(UserContextDto dto) {
        if (dto == null) return null;

        return UserContext.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .contextType(dto.getContextType())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .roleId(dto.getRoleId())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("User context not found: " + id);
    }

    @Override
    public ListResultset<UserContextDto> createOrReplaceBulk(
            UUID deletedBy,
            List<UserContextDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented yet");
    }

    @Override
    protected UserContextDto removeExcludedFields(UserContextDto dto) {
        return dto;
    }


    public ListResultset<UserContextDetailDto> getDetailed(QueryOptions options) {

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());


        StringBuilder sqlBuilder = new StringBuilder("""
            select uc.*, r.name, r.description,r."isSystem" from user_contexts uc
                left  join roles r on r.id = uc."roleId"
            """);
        if (!options.orderBy().isEmpty()) {

            String orderClause = options.orderBy().entrySet().stream()
                    .map(e -> {
                        String field = e.getKey();
                        String direction = e.getValue().toUpperCase();

                        // validate
                        if (!UserDirectoryQueryPolicy.isAllowedSort(field)) {
                            return null;
                        }

                        String column = UserDirectoryQueryPolicy.resolveSortColumn(field);
                        if (column == null) {
                            return null;
                        }

                        return column + " " + direction;
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.joining(", "));

            if (!orderClause.isBlank()) {
                sqlBuilder.append(" ORDER BY ").append(orderClause);
            }
        }
        String sql = sqlBuilder.toString();
        String countSql = """
        select count(*)
        from user_contexts uc
                left  join roles r on r.id = uc."roleId"
        """;
        System.out.println("Executing directory query with SQL: " + sql);
        Map<String, Object> params = Map.of(
                "userId",
                options.filters().get("userId")
        );

        return ds.findAllBySql(
                sql,
                countSql,
                params,
                Map.of(
                        "userId", "uc.userId"
                ),
                Collections.emptyMap(),
                options.page(),
                options.size(),
                (rs, rowNum) -> {
                    try {
                        return UserContextDetailDto.builder()
                                .id(rs.getObject("id", UUID.class))
                                .userId(rs.getObject("userId", UUID.class))
                                .contextType(rs.getString("contextType"))
                                .metadata(OBJECT_MAPPER.readTree(rs.getString("metadata")))
                                .roleId(rs.getObject("roleId", UUID.class))
                                .name(rs.getString("name"))
                                .description(rs.getString("description"))
                                .isSystem(rs.getBoolean("isSystem"))
                                .build();
                    } catch (IOException e) {
                        throw new SQLException("Failed to parse metadata JSON", e);
                    }
                }
        );
    }
}