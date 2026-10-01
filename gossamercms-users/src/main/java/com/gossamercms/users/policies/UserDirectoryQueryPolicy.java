package com.gossamercms.users.policies;

import java.util.Map;
import java.util.Set;

public final class UserDirectoryQueryPolicy {

    private UserDirectoryQueryPolicy() {
        // no instances
    }

    /**
     * Optional: map API sort keys -> SQL columns
     * (safer than exposing DB names directly)
     */
    public static final Map<String, String> SORT_MAPPING = Map.of(
            "firstName", "u.firstname",
            "firstname", "u.firstname",
            "lastName", "u.lastname",
            "lastname", "u.lastname",
            "createdOn", "u.\"createdOn\"",
            "email", "email",
            "lastLoginAt", "li.\"lastLoginAt\"",
            "contextType", "uc.\"contextType\"",
            "id","u.id"
    );

    /**
     * Whitelisted sortable API keys.
     */
    public static final Set<String> ALLOWED_SORTS = SORT_MAPPING.keySet();

    public static boolean isAllowedSort(String field) {
        return ALLOWED_SORTS.contains(field);
    }

    public static String resolveSortColumn(String field) {
        return SORT_MAPPING.get(field);
    }
}
