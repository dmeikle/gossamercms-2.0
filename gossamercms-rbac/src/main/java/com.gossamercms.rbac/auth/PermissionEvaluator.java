package com.gossamercms.rbac.auth;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;


public class PermissionEvaluator {

    private final PermissionResolver resolver;
    private final PermissionCache cache;

    public PermissionEvaluator(PermissionResolver resolver, PermissionCache cache) {
        this.resolver = resolver;
        this.cache = cache;
    }

    public boolean hasPermission(UUID userId, String permission) {

        Set<String> cached = cache.get(userId);
        if (cached != null) {
            return cached.contains(permission);
        }

        Set<String> resolved = resolver.resolvePermissions(userId);
        cache.put(userId, resolved);

        return resolved.contains(permission);
    }
}