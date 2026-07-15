package com.gossamercms.users.adapters.inbound;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.mvc.http.RequestContext;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.domain.UserContext;
import com.gossamercms.users.handlers.UserContextsHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for authenticated users to list and select their contexts.
 *
 * After login (but before issuing the final CMS JWT), users must select which
 * context they want to work in. This controller handles that selection flow.
 */
@RestController
@RequestMapping("/auth/contexts")
public class UserContextsController  extends BaseController<UserContext, UserContextDto> {


    public UserContextsController(UserContextsHandler handler) {
        super(handler);
    }

    /**
     * GET /auth/contexts
     *
     * Returns all contexts and account mappings available to the logged-in user.
     * This is called after the user logs in but before they select a context.
     */
    @GetMapping("my-contexts")
    public Object listAvailableContexts(@CurrentUser JwtUser user) {
        System.out.println("here is JWT User");
        System.out.println(user);
        RequestContext ctx = new RequestContext(user.getUserId(), user.getUsername(), Map.of());
        return  ((UserContextsHandler) handler).listAvailableContexts(ctx);
    }

    /**
     * POST /auth/contexts/select
     *
     * Request body:
     * {
     *   "contextId": "...",
     *   "accountId": "..."
     * }
     *
     * Validates that the selected context and account belong to the user,
     * then generates and returns a JWT token with context claims embedded.
     *
     * The returned JWT should be used for all subsequent API calls to indicate
     * which context the user is operating in.
     */


    @GetMapping("/whoami")
    public Object whoAmI(@CurrentUser JwtUser user) {
        return user;
    }
}

