package com.gossamercms.users.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.mvc.http.RequestContext;
import com.gossamercms.mvc.http.ResponseBuilder;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.security.services.JwtService;
import com.gossamercms.security.shared.factories.JwtClaimsFactory;
import com.gossamercms.users.api.AccountMappingDto;
import com.gossamercms.users.api.UserContextDetailDto;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.api.responses.SwitchContextResponseDto;
import com.gossamercms.users.data.AccountMappingsDbService;
import com.gossamercms.users.data.UserContextsDbService;
import com.gossamercms.users.data.UsersDbService;
import com.gossamercms.users.domain.UserContext;


import java.util.*;

@ModuleHandler
public class UserContextsHandler extends BaseHandler<UserContext, UserContextDto> {

    private final AccountMappingsDbService accountMappingsDb;
    private final JwtService jwtService;
    private final UsersDbService usersDb;

    public UserContextsHandler(
            UserContextsDbService userContextsDb,
            AccountMappingsDbService accountMappingsDb,
            UsersDbService usersDb,
            JwtService jwtService) {
        super(userContextsDb);
        this.accountMappingsDb = accountMappingsDb;
        this.jwtService = jwtService;
        this.usersDb = usersDb;
    }

    /**
     * GET /auth/contexts
     *
     * Returns all contexts + accounts for the authenticated user.
     * This is used after login but before JWT issuance.
     */
    public Object listAvailableContexts(RequestContext ctx) {

        UUID userId = ctx.userId();
        if (userId == null) {
            return ResponseBuilder.unauthorized("No authenticated user");
        }

        List<UserContextDto> contexts =
                db.getAll(QueryOptions.builder()
                                .filters(Map.of("userId", userId))
                                .page(1)
                                .size(100)
                                .build())
                        .list();

        List<AccountMappingDto> accounts = new ArrayList<>();

        for (UserContextDto context : contexts) {
            accounts.addAll(
                    accountMappingsDb.getAll(QueryOptions.builder()
                                    .filters(Map.of("userContextId", context.getId()))
                                    .build())
                            .list()
            );
        }

        return ResponseBuilder.ok(Map.of(
                "userId", userId,
                "contexts", contexts,
                "accounts", accounts
        ));
    }



    public Object selectContext(JwtUser jwtUser, UserContextDto userContext, String roleName, String identifier, String[] permissions) {
        // Implementation for selecting context with role and permissions
        UUID userId = jwtUser.getUserId();
        if (userId == null) {
            return ResponseBuilder.unauthorized("No authenticated user");
        }

        UUID contextId = userContext.getId();
       // UUID accountId = ctx.bodyAsUuid("accountId");

        if (contextId == null /*|| accountId == null*/) {
            return ResponseBuilder.badRequest("contextId and accountId are required");
        }

        // Validate context belongs to user

        if (userContext == null || !userContext.getUserId().equals(userId)) {
            return ResponseBuilder.notFound("Context not found for user");
        }

//        // Validate account mapping belongs to context/user
//        AccountMappingDto account = accountMappingsDb.get(Map.of("id", accountId));
//
//        if (account == null || !account.getUserContextId().equals(contextId)) {
//            return ResponseBuilder.notFound("Account mapping not found for user");
//        }

        // Load user (needed for claims factory consistency)
        UserDto user = usersDb.getById(userId);

        // Generate JWT via factory
        Map<String, Object> claims =
                JwtClaimsFactory.toClaims(user.getId(), userContext.getId(), jwtUser.getSessionId(),  identifier, roleName, permissions);

        String token = jwtService.generateToken(userId, claims);

        return ResponseBuilder.ok(new SwitchContextResponseDto(
                userId,
                userContext.getId(),
                roleName,
                token
        ));
    }

    public ListResultset<UserContextDetailDto> getDetailed(QueryOptions options) {
        return ((UserContextsDbService) db).getDetailed(options);
    }
}