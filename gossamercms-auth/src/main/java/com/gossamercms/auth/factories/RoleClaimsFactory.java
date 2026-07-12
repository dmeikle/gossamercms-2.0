package com.gossamercms.auth.factories;

import com.gossamercms.auth.data.PermissionsDbService;
import com.gossamercms.auth.dtos.PermissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleClaimsFactory {

    private final PermissionsDbService permissionsDbService;

    public String[] getPermissionsByUserContext(UUID userContextId) {
        return permissionsDbService
                .getAllPermissionsByUserContextId(userContextId)
                .list()
                .stream()
                .map(PermissionDto::getName)
                .toArray(String[]::new);
    }
}
