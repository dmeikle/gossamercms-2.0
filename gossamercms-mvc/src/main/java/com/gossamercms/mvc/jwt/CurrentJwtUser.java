package com.gossamercms.mvc.jwt;


import java.util.UUID;

public interface CurrentJwtUser {

    UUID getUserId();

    default boolean isAuthenticated() {
        return getUserId() != null;
    }

    String getIdentifier();

    String getSessionId();

    String[] getRoles();

    String[] getPermissions();

    String getUserContextId();

}