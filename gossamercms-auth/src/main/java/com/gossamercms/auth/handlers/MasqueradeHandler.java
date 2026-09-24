package com.gossamercms.auth.handlers;

import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.data.RolesDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.factories.RoleClaimsFactory;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.security.services.JwtService;
import com.gossamercms.security.shared.factories.JwtClaimsFactory;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.data.UserContextsDbService;
import com.gossamercms.users.data.UsersDbService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleHandler
@RequiredArgsConstructor
public class MasqueradeHandler {
    private final LoginIdentityDbService identityDb;
    private final UsersDbService usersDb;
    private final UserContextsDbService userContextsDb;
    private final JwtService jwtService;
    private final RoleClaimsFactory roleClaimsFactory;
    private final RolesDbService rolesDbService;

    public LoginResult start(JwtUser jwtUser, UserDto masqueradingUser, String fallbackSessionId) {
        JwtUser authenticatedUser = requireAuthenticatedUser(jwtUser);

        if (authenticatedUser.getUserId().equals(masqueradingUser.getId())) {
            throw new IllegalStateException("User is already authenticated as the selected user");
        }

        UUID adminUserId = authenticatedUser.isMasquerading()
                ? authenticatedUser.getAdminUserId()
                : authenticatedUser.getUserId();

        String adminUserContextId = authenticatedUser.isMasquerading()
                ? authenticatedUser.getAdminUserContextId()
                : authenticatedUser.getUserContextId();

        String adminIdentifier = authenticatedUser.isMasquerading()
                ? authenticatedUser.getAdminIdentifier()
                : authenticatedUser.getIdentifier();

        String[] adminRoles = authenticatedUser.isMasquerading()
                ? authenticatedUser.getAdminRoles()
                : authenticatedUser.getRoles();

        String[] adminPermissions = authenticatedUser.isMasquerading()
                ? authenticatedUser.getAdminPermissions()
                : authenticatedUser.getPermissions();

        Map<String, Object> masqueradeClaims = new HashMap<>();
        masqueradeClaims.put("adminUserId", adminUserId.toString());
        masqueradeClaims.put("masqueradingUserId", masqueradingUser.getId().toString());

        if (adminUserContextId != null) {
            masqueradeClaims.put("adminUserContextId", adminUserContextId);
        }

        if (adminIdentifier != null) {
            masqueradeClaims.put("adminIdentifier", adminIdentifier);
        }

        if (adminRoles.length > 0) {
            masqueradeClaims.put("adminRoles", adminRoles);
        }

        if (adminPermissions.length > 0) {
            masqueradeClaims.put("adminPermissions", adminPermissions);
        }

        return issueLoginResult(
                masqueradingUser,
                resolveSessionId(authenticatedUser, fallbackSessionId),
                null,
                masqueradeClaims
        );
    }

    public LoginResult stop(JwtUser jwtUser, String fallbackSessionId) {
        JwtUser authenticatedUser = requireAuthenticatedUser(jwtUser);

        if (!authenticatedUser.isMasquerading()) {
            throw new IllegalStateException("User is not currently masquerading");
        }

        UserDto adminUser = usersDb.getById(authenticatedUser.getAdminUserId());
        UUID adminUserContextId = parseUuid(authenticatedUser.getAdminUserContextId());

        return issueLoginResult(
                adminUser,
                resolveSessionId(authenticatedUser, fallbackSessionId),
                adminUserContextId,
                Map.of()
        );
    }

    private LoginResult issueLoginResult(
            UserDto user,
            String sessionId,
            UUID preferredContextId,
            Map<String, Object> extraClaims
    ) {
        LoginIdentityDto identity = identityDb.get(Map.of("userId", user.getId()));
        List<UserContextDto> userContexts = userContextsDb.getAll(QueryOptions.builder()
                .page(1)
                .size(-1)
                .filters(Map.of("userId", user.getId()))
                .build())
                .list();

        UserContextDto selectedContext = resolveContext(user, userContexts, preferredContextId);
        String[] permissionNames = roleClaimsFactory.getPermissionsByUserContext(selectedContext.getId());
        RoleDto role = rolesDbService.getById(selectedContext.getRoleId());

        String token = jwtService.generateToken(
                user.getId(),
                JwtClaimsFactory.toClaims(
                        user.getId(),
                        selectedContext.getId(),
                        sessionId,
                        identity.getIdentifier(),
                        role.getName(),
                        permissionNames,
                        extraClaims
                )
        );

        return new LoginResult(
                user,
                identity,
                token,
                userContexts
        );
    }

    private UserContextDto resolveContext(UserDto user, List<UserContextDto> userContexts, UUID preferredContextId) {
        if (preferredContextId != null) {
            return userContexts.stream()
                    .filter(ctx -> preferredContextId.equals(ctx.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No user context found for user " + user.getId() + " and context " + preferredContextId
                    ));
        }

        return userContexts.stream()
                .filter(ctx -> Boolean.TRUE.equals(ctx.getMetadata().get("defaultContext")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No default user context found for user " + user.getId()
                ));
    }

    private JwtUser requireAuthenticatedUser(JwtUser jwtUser) {
        if (jwtUser == null || !jwtUser.isAuthenticated()) {
            throw new UnauthorizedStatusException();
        }

        return jwtUser;
    }

    private String resolveSessionId(JwtUser jwtUser, String fallbackSessionId) {
        return jwtUser.getSessionId() == null || jwtUser.getSessionId().isBlank()
                ? fallbackSessionId
                : jwtUser.getSessionId();
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return UUID.fromString(value);
    }
}
