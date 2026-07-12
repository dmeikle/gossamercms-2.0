package com.gossamercms.users.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.*;
import com.gossamercms.users.api.UserDetailDto;
import com.gossamercms.users.api.UserDirectoryDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.domain.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.gossamercms.users.policies.UserDirectoryQueryPolicy;

@ModuleDbService
public class UsersDbService extends BaseDbService<User, UserDto> {

    public UsersDbService(DataSourceManager dsManager) {
        super(User.class, UserDto.class, dsManager);
        System.out.println("************************* UsersDbService constructed *********************");
    }

    @Override
    protected UserDto mapToDto(User entity) {
        if (entity == null) return null;

        return UserDto.builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .status(entity.getStatus())
                .ipAddress(entity.getIpAddress())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    @Override
    protected User mapToEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .status(dto.getStatus())
                .ipAddress(dto.getIpAddress())
                .createdOn(dto.getCreatedOn())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("User not found: " + id);
    }

    @Override
    public ListResultset<UserDto> createOrReplaceBulk(
            UUID deletedBy,
            List<UserDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented yet");
    }

    @Override
    protected UserDto removeExcludedFields(UserDto dto) {
        return dto;
    }

    public ListResultset<UserDirectoryDto> directory(QueryOptions options) {

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        StringBuilder sqlBuilder = new StringBuilder("""
            select
                u.id,
                u.firstname,
                u.lastname,
                uc."contextType",
                li."lastLoginAt",
                li.identifier as email,
                ut."countryCode" as "phoneCountryCode",
                ut."numberRaw" as "phoneNumber"
            from users u
            inner join user_contexts uc
                on u.id = uc."userId"
            inner join login_identities li
                on li."userId" = u.id
            inner join user_telephone ut
                on ut."userId" = u.id
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
        from users u
        inner join user_contexts uc
            on u.id = uc."userId"
        inner join login_identities li
            on li."userId" = u.id
        inner join user_telephone ut
            on ut."userId" = u.id
        """;
System.out.println("Executing directory query with SQL: " + sql);
        Map<String, Object> params = Map.of(
                "contextType",
                options.filters().getOrDefault("contextType", "default")
        );

        return ds.findAllBySql(
                sql,
                countSql,
                params,
                Map.of(
                        "contextType", "uc.contextType"
                ),
                Collections.emptyMap(),
                options.page(),
                options.size(),
                (rs, rowNum) -> UserDirectoryDto.builder()
                        .id(rs.getObject("id", UUID.class))
                        .firstname(rs.getString("firstname"))
                        .lastname(rs.getString("lastname"))
                        .email(rs.getString("email"))
                        .contextType(rs.getString("contextType"))
                        .lastLoginAt(
                                rs.getTimestamp("lastLoginAt") != null
                                        ? rs.getTimestamp("lastLoginAt").toInstant()
                                        : null
                        )
                        .phoneCountryCode(rs.getString("phoneCountryCode"))
                        .phoneNumber(rs.getString("phoneNumber"))
                        .build()
        );
    }

    public UserDetailDto getUserDetail(UUID userId) {

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        String sql = """
                select
                    u.id,
                    u.firstname,
                    u.lastname,
                    uc."contextType",
                    li."lastLoginAt",
                    li.identifier as email,
                    ut."countryCode" as "phoneCountryCode",
                    ut."numberRaw" as "phoneNumber",
                
                    ua.address1,
                    ua.address2,
                    ua.city,
                    ua."stateProvince",
                    ua."postalCode",
                    ua."countryCode",
                    ua."isDefault"
                
                from users u
                
                         inner join user_contexts uc
                                    on u.id = uc."userId"
                                        and uc."contextType" = 'default'
                
                         inner join login_identities li
                                    on li."userId" = u.id
                
                         inner join user_telephone ut
                                    on ut."userId" = u.id
                
                -- ✅ pick default address if exists, otherwise first address
                         left join lateral (
                    select *
                    from user_addresses a
                    where a."userId" = u.id
                    order by a."isDefault" desc
                    limit 1
                    ) ua on true
                
                where u.id = :userId
                """;

        return ds.findOneBySql(
                sql,
                Map.of("userId", userId),
                (rs, rowNum) -> new UserDetailDto(
                        rs.getObject("id", UUID.class),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("contextType"),
                        rs.getTimestamp("lastLoginAt") != null
                                ? rs.getTimestamp("lastLoginAt").toInstant()
                                : null,
                        rs.getString("email"),
                        rs.getString("phoneCountryCode"),
                        rs.getString("phoneNumber"),

                        rs.getString("address1"),
                        rs.getString("address2"),
                        rs.getString("city"),
                        rs.getString("stateProvince"),
                        rs.getString("postalCode"),
                        rs.getString("countryCode"),
                        rs.getObject("isDefault", Boolean.class)
                )
        );
    }
}