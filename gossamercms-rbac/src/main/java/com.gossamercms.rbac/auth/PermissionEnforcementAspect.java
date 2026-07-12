package com.gossamercms.rbac.auth;

import com.gossamercms.rbac.auth.exceptions.ForbiddenException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

@Aspect
@Component
public class PermissionEnforcementAspect {

    private final PermissionResolver resolver;
    private final CurrentUserProvider currentUserProvider;

    public PermissionEnforcementAspect(
            PermissionResolver resolver,
            CurrentUserProvider currentUserProvider
    ) {
        this.resolver = resolver;
        this.currentUserProvider = currentUserProvider;
    }

    @Before("@within(com.gossamercms.rbac.auth.RequirePermission) || " +
            "@annotation(com.gossamercms.rbac.auth.RequirePermission) || " +
            "@within(com.gossamercms.rbac.auth.RequireRole) || " +
            "@annotation(com.gossamercms.rbac.auth.RequireRole) || " +
            "@within(com.gossamercms.rbac.auth.RequireSuperAdmin) || " +
            "@annotation(com.gossamercms.rbac.auth.RequireSuperAdmin)")
    public void enforce(JoinPoint jp) {

        UUID userId = currentUserProvider.getCurrentUserId();
        Set<String> permissions = resolver.resolvePermissions(userId);

        Method method = ((MethodSignature) jp.getSignature()).getMethod();

        // SUPERADMIN bypass
        if (permissions.contains("*")) {
            return;
        }

        // Check @RequireSuperAdmin
        if (method.isAnnotationPresent(RequireSuperAdmin.class) ||
                method.getDeclaringClass().isAnnotationPresent(RequireSuperAdmin.class)) {
            throw new ForbiddenException("Super admin required");
        }

        // Check @RequireRole
        RequireRole role = method.getAnnotation(RequireRole.class);
        if (role == null) {
            role = method.getDeclaringClass().getAnnotation(RequireRole.class);
        }
        if (role != null) {
            if (!permissions.contains("role:" + role.value())) {
                throw new ForbiddenException("Role required: " + role.value());
            }
        }

        // Check @RequirePermission
        RequirePermission perm = method.getAnnotation(RequirePermission.class);
        if (perm == null) {
            perm = method.getDeclaringClass().getAnnotation(RequirePermission.class);
        }
        if (perm != null) {
            if (!permissions.contains(perm.value())) {
                throw new ForbiddenException("Missing permission: " + perm.value());
            }
        }
    }
}