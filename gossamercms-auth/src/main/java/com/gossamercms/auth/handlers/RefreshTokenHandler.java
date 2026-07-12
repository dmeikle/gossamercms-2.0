package com.gossamercms.auth.handlers;

import com.gossamercms.auth.adapters.AuthenticationProvider;
import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.data.RefreshTokensDbService;
import com.gossamercms.auth.data.RolesDbService;
import com.gossamercms.auth.dtos.*;
import com.gossamercms.auth.factories.RoleClaimsFactory;
import com.gossamercms.auth.factories.TokenFactory;
import com.gossamercms.auth.models.RefreshToken;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.security.services.JwtService;
import com.gossamercms.security.shared.factories.JwtClaimsFactory;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.data.UserContextsDbService;
import com.gossamercms.users.data.UsersDbService;
import gossamercms.globals.events.users.UserLoggedInEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

@ModuleHandler
public class RefreshTokenHandler  {

    private final RefreshTokensDbService dbService;
    private final AuthenticationProvider authProvider;
    private final LoginIdentityDbService identityDb;
    private final UsersDbService usersDb;
    private final UserContextsDbService userContextsDb;
    private final JwtService jwtService;
    private final RoleClaimsFactory roleClaimsFactory;
    private final RolesDbService rolesDbService;
    private final ApplicationEventPublisher eventPublisher;

    public RefreshTokenHandler(
            RefreshTokensDbService dbService,
            AuthenticationProvider authProvider,
            LoginIdentityDbService identityDb,
            UsersDbService usersDb,
            UserContextsDbService userContextsDb,
            RolesDbService rolesDbService,
            RoleClaimsFactory roleClaimsFactory,
            JwtService jwtService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.dbService = dbService;
        this.authProvider = authProvider;
        this.identityDb = identityDb;
        this.usersDb = usersDb;
        this.userContextsDb = userContextsDb;
        this.rolesDbService = rolesDbService;
        this.roleClaimsFactory = roleClaimsFactory;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public RefreshResult refresh(String refreshToken, String sessionId) {

        RefreshTokenDto existing =
                dbService.get(Map.of("token", refreshToken));
//                        .orElseThrow(() ->
//                                new BadCredentialsException(
//                                        "Invalid refresh token"));

        if (existing.isRevoked()
                || existing.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new BadCredentialsException(
                    "Refresh token invalid");
        }
        // 2. Find identity by provider + providerUserId
        LoginIdentityDto identity = identityDb.findByProviderAndIdentifier(
                "auth0",
                existing.getUsername()
        );

        if (identity == null) {
            System.out.println("Identity not found for provider 'auth0' and identifier: " +  existing.getUsername());
            throw new RuntimeException("Identity not found for Auth0 user");
        }

        // 3. Load user
        UserDto user = usersDb.getById(identity.getUserId());

        //4. Load UserContextList
        ListResultset<UserContextDto> userContexts = userContextsDb.getAll(QueryOptions.builder()
                .page(1)
                .size(-1)
                .filters(Map.of("userId", user.getId()))
                .build());

        UserContextDto defaultContext = userContexts.list().stream()
                .filter(ctx -> {
                    Object value = ctx.getMetadata().get("defaultContext");
                    return Boolean.TRUE.equals(value);
                })
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No default user context found for user " + user.getId())
                );
        // 5. Load Permissions
        String [] permissionNames = roleClaimsFactory.getPermissionsByUserContext(defaultContext.getId());
        RoleDto role = rolesDbService.getById(defaultContext.getRoleId());

        // 6. Generate JWT
        String token = jwtService.generateToken(
                user.getId(),
                JwtClaimsFactory.toClaims(
                        user.getId(),
                        defaultContext.getId(),
                        sessionId,
                        identity.getIdentifier(),
                        role.getName(),
                        permissionNames
                )
        );
        eventPublisher.publishEvent(new UserLoggedInEvent(
                user.getId(),
                Instant.now()
        ));

        existing.setRevoked(true);
        dbService.update(user.getId(), existing, Map.of("id",existing.getId()));
        String newRefreshTokenId = TokenFactory.generateRefreshToken();

        RefreshToken replacement =
                new RefreshToken();

       // replacement.setId(UUID.randomUUID());
        replacement.setToken(newRefreshTokenId);
        replacement.setUsername(
                existing.getUsername());
        replacement.setExpiresAt(
                Instant.now().plus(30, ChronoUnit.DAYS));
        replacement.setRevoked(false);

        RefreshTokenDto newRefreshToken = dbService.create(user.getId(), replacement.toDto());

        return new RefreshResult(
                new LoginResult(
                        user,
                        identity,
                        token,
                        userContexts.list()
                ),
                newRefreshToken
        );
    }

    public RefreshTokenDto create(LoginResult loginResult) {
        String newRefreshToken =TokenFactory.generateRefreshToken();

        RefreshToken replacement =
                new RefreshToken();

        replacement.setId(UUID.randomUUID());
        replacement.setToken(newRefreshToken);
        replacement.setUsername(
                loginResult.identity().getIdentifier());
        replacement.setExpiresAt(
                Instant.now().plus(30, ChronoUnit.DAYS));
        replacement.setRevoked(false);

        return dbService.create(loginResult.user().getId(), replacement.toDto());
    }
}
