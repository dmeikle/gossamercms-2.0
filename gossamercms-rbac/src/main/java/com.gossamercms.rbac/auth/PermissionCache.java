package com.gossamercms.rbac.auth;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;


public class PermissionCache {

    private static class CacheEntry {
        Set<String> permissions;
        Instant expiresAt;
    }

    private final Map<UUID, CacheEntry> cache = new HashMap<>();
    private final long ttlSeconds;

    public PermissionCache(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public Set<String> get(UUID userId) {
        CacheEntry entry = cache.get(userId);
        if (entry == null) return null;
        if (Instant.now().isAfter(entry.expiresAt)) {
            cache.remove(userId);
            return null;
        }
        return entry.permissions;
    }

    public void put(UUID userId, Set<String> permissions) {
        CacheEntry entry = new CacheEntry();
        entry.permissions = permissions;
        entry.expiresAt = Instant.now().plusSeconds(ttlSeconds);
        cache.put(userId, entry);
    }

    public void invalidate(UUID userId) {
        cache.remove(userId);
    }
}