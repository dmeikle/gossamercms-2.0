package com.gossamercms.auth.data;

import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.models.LoginIdentity;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceAdapter;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@ModuleDbService
public class LoginIdentityDbService extends BaseDbService<LoginIdentity, LoginIdentityDto> {



    public LoginIdentityDbService(DataSourceManager dsManager) {
        super(LoginIdentity.class, LoginIdentityDto.class, dsManager);
    }

    @Override
    protected LoginIdentityDto mapToDto(LoginIdentity entity) {
        if (entity == null) return null;

        return LoginIdentityDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .identifier(entity.getIdentifier())
                .provider(entity.getProvider())
                .providerUserId(entity.getProviderUserId())
                .isPrimary(entity.isPrimary())
                .createdOn(entity.getCreatedOn())
                .lastLoginAt(entity.getLastLoginAt())
                .build();
    }

    @Override
    protected LoginIdentity mapToEntity(LoginIdentityDto dto) {
        if (dto == null) return null;

        return LoginIdentity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .type(dto.getType())
                .identifier(dto.getIdentifier())
                .provider(dto.getProvider())
                .providerUserId(dto.getProviderUserId())
                .isPrimary(dto.isPrimary())
                .createdOn(dto.getCreatedOn())
                .lastLoginAt(dto.getLastLoginAt())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("LoginIdentity not found: " + id);
    }

    @Override
    public ListResultset<LoginIdentityDto> createOrReplaceBulk(
            UUID deletedBy,
            List<LoginIdentityDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented for LoginIdentity");
    }

    @Override
    public LoginIdentityDto removeExcludedFields(LoginIdentityDto dto) {
        // No excluded fields for now
        return dto;
    }

    public boolean emailExists(String email) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = Map.of("identifier", email);

        Map<String, Object> row = (Map<String, Object>) ds.findOne(meta.table(), filter);

        return row != null;
    }


    public LoginIdentityDto findByProviderAndProviderUserId(String provider, String providerUserId) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        Map<String, Object> row = (Map<String, Object>)ds.findOne(
                LoginIdentity.META.table(),
                Map.of(
                        "provider", provider,
                        "providerUserId", providerUserId
                )
        );

        if (row == null) return null;

        return LoginIdentityDto.builder()
                .id((UUID) row.get("id"))
                .userId((UUID) row.get("userId"))
                .type((String) row.get("type"))
                .identifier((String) row.get("identifier"))
                .passwordHash((String) row.get("passwordHash"))
                .provider((String) row.get("provider"))
                .providerUserId((String) row.get("providerUserId"))
                .isPrimary((Boolean) row.get("isPrimary"))
                .createdOn(toInstant(row.get("createdOn")))
                .lastLoginAt(toInstant( row.get("lastLoginAt")))
                .build();
    }


    public LoginIdentityDto findByProviderAndIdentifier(String provider, String identifier) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        Map<String, Object> row = (Map<String, Object>)ds.findOne(
                LoginIdentity.META.table(),
                Map.of(
                        "provider", provider,
                        "identifier", identifier
                )
        );

        if (row == null) return null;

        return LoginIdentityDto.builder()
                .id((UUID) row.get("id"))
                .userId((UUID) row.get("userId"))
                .type((String) row.get("type"))
                .identifier((String) row.get("identifier"))
                .passwordHash((String) row.get("passwordHash"))
                .provider((String) row.get("provider"))
                .providerUserId((String) row.get("providerUserId"))
                .isPrimary((Boolean) row.get("isPrimary"))
                .createdOn(toInstant(row.get("createdOn")))
                .lastLoginAt(toInstant( row.get("lastLoginAt")))
                .build();
    }
}
