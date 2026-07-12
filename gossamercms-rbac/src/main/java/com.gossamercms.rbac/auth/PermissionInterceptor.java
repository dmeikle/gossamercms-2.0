package com.gossamercms.rbac.auth;

import jakarta.servlet.http.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionEvaluator evaluator;

    public PermissionInterceptor(PermissionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequirePermission annotation =
                method.getMethodAnnotation(RequirePermission.class);

        if (annotation == null) {
            annotation = method.getBeanType().getAnnotation(RequirePermission.class);
        }

        if (annotation == null) {
            return true;
        }

        String required = annotation.value();

        // You will replace this with your actual auth user ID extraction
        UUID userId = (UUID) req.getAttribute("authUserId");

        if (userId == null) {
            res.sendError(401, "Unauthorized");
            return false;
        }

        if (!evaluator.hasPermission(userId, required)) {
            System.out.println("****************** permission denied in PermissionInterceptor ***********************");
            res.sendError(403, "Forbidden");
            return false;
        }

        return true;
    }
}