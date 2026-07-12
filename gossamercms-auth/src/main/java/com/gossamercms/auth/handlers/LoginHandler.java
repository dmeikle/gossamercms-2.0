package com.gossamercms.auth.handlers;


import com.gossamercms.auth.adapters.AuthResult;
import com.gossamercms.auth.adapters.AuthenticationProvider;
import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.data.RolesDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.LoginRequestDto;
import com.gossamercms.auth.dtos.responses.LoginResponseDto;
import com.gossamercms.auth.factories.RoleClaimsFactory;
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

import java.time.Instant;
import java.util.Map;


@ModuleHandler
public class LoginHandler {

    private final AuthenticationProvider authProvider;
    private final LoginIdentityDbService identityDb;
    private final UsersDbService usersDb;
    private final UserContextsDbService userContextsDb;
    private final JwtService jwtService;
    private final RoleClaimsFactory roleClaimsFactory;
    private final RolesDbService rolesDbService;
    private final ApplicationEventPublisher eventPublisher;

    public LoginHandler(
            AuthenticationProvider authProvider,
            LoginIdentityDbService identityDb,
            UsersDbService usersDb,
            UserContextsDbService userContextsDb,
            RolesDbService rolesDbService,
            RoleClaimsFactory roleClaimsFactory,
            JwtService jwtService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.authProvider = authProvider;
        this.identityDb = identityDb;
        this.usersDb = usersDb;
        this.userContextsDb = userContextsDb;
        this.rolesDbService = rolesDbService;
        this.roleClaimsFactory = roleClaimsFactory;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    public LoginResult handle(LoginRequestDto req, String sessionId) {

        System.out.println("email " + req.email() + " password " + req.password());
        // 1. Authenticate with Auth0
        AuthResult authResult =  authProvider.authenticate(req.email(), req.password());


        // 2. Find identity by provider + providerUserId
        LoginIdentityDto identity = identityDb.findByProviderAndProviderUserId(
                "auth0",
                authResult.claims().get("provider_user_id").toString()
        );

        if (identity == null) {
            System.out.println("Identity not found for provider 'auth0' and providerUserId: " + "auth0UserId");
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

        //5. Return result
        return new LoginResult(
                user,
                identity,
                token,
                userContexts.list()
        );

    }

}