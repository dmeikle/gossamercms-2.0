package com.gossamercms.users.policies;

import java.util.Map;
import java.util.Set;

public final class UserDirectoryQueryPolicy {

    private UserDirectoryQueryPolicy() {
        // no instances
    }

    /**
     * Whitelisted sortable columns (DB/SQL level names)
     */
    public static final Set<String> ALLOWED_SORTS = Set.of(
            "firstname",
            "lastname",
            "email",
            "lastLoginAt",
            "contextType",
            "id"
    );

    /**
     * Optional: map API sort keys -> SQL columns
     * (safer than exposing DB names directly)
     */
    public static final Map<String, String> SORT_MAPPING = Map.of(
            "firstName", "u.firstname",
            "lastName", "u.lastname",
            "email", "email",
            "lastLoginAt", "li.\"lastLoginAt\"",
            "contextType", "uc.\"contextType\"",
            "id","u.id"
    );

    public static boolean isAllowedSort(String field) {
        return ALLOWED_SORTS.contains(field);
    }

    public static String resolveSortColumn(String field) {
        return SORT_MAPPING.get(field);
    }
}
