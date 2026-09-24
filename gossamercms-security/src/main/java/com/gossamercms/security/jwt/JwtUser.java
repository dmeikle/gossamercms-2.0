package com.gossamercms.security.jwt;

import com.gossamercms.mvc.jwt.CurrentJwtUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import java.util.stream.Stream;

@Getter
public class JwtUser implements UserDetails, CurrentJwtUser {

    private final UUID userId;
    private final String identifier;
    private final String sessionid;
    private final String[] roles;
    private final String[] permissions;
    private final String userContextId;
    private final UUID adminUserId;
    private final UUID masqueradingUserId;
    private final String adminUserContextId;
    private final String adminIdentifier;
    private final String[] adminRoles;
    private final String[] adminPermissions;
    private Collection<GrantedAuthority> authorities;

    public JwtUser(
            UUID userId,
            String identifier,
            String[] roles,
            String[] permissions,
            String sessionId
    ) {
        this(userId, identifier, roles, permissions, sessionId, null);
    }

    public JwtUser(
            UUID userId,
            String identifier,
            String[] roles,
            String[] permissions,
            String sessionId,
            String userContextId
    ) {
        this(
                userId,
                identifier,
                roles,
                permissions,
                sessionId,
                userContextId,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public JwtUser(
            UUID userId,
            String identifier,
            String[] roles,
            String[] permissions,
            String sessionId,
            String userContextId,
            UUID adminUserId,
            UUID masqueradingUserId,
            String adminUserContextId,
            String adminIdentifier,
            String[] adminRoles,
            String[] adminPermissions
    ) {
        this.userId = userId;
        this.identifier = identifier;
        this.roles = roles == null ? new String[0] : roles;
        this.permissions = permissions == null ? new String[0] : permissions;
        this.sessionid = sessionId;
        this.userContextId = userContextId;
        this.adminUserId = adminUserId;
        this.masqueradingUserId = masqueradingUserId;
        this.adminUserContextId = adminUserContextId;
        this.adminIdentifier = adminIdentifier;
        this.adminRoles = adminRoles == null ? new String[0] : adminRoles;
        this.adminPermissions = adminPermissions == null ? new String[0] : adminPermissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Stream.concat(
                Arrays.stream(roles)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                Arrays.stream(permissions)
                        .map(SimpleGrantedAuthority::new)
        ).toList();
    }

    public boolean hasRole(String role) {
        return Arrays.stream(roles)
                .anyMatch(role::equals);
    }

    public boolean hasPermission(String permission) {
        return Arrays.stream(permissions)
                .anyMatch(permission::equals);
    }

    @Override
    public String getUsername() {
        return identifier;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getUserContextId() {
        return userContextId;
    }



    @Override
    public boolean isAuthenticated() {
        return CurrentJwtUser.super.isAuthenticated();
    }

    public String getSessionId() {
        return sessionid;
    }

    public boolean isMasquerading() {
        return adminUserId != null && masqueradingUserId != null;
    }
}