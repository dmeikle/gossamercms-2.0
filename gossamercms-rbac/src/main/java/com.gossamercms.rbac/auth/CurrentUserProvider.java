package com.gossamercms.rbac.auth;

import java.util.UUID;

public interface CurrentUserProvider {
    UUID getCurrentUserId();
}