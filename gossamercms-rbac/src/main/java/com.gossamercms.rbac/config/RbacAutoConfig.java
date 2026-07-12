package com.gossamercms.rbac.config;

import com.gossamercms.mvc.autoconfig.*;
import com.gossamercms.rbac.auth.*;
import com.gossamercms.rbac.auth.exceptions.ForbiddenException;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@AutoConfiguration
@ComponentScan(basePackages = {
//        "com.gossamercms.rbac.services",
//        "com.gossamercms.rbac.data",
        "com.gossamercms.rbac.auth",
//        "com.gossamercms.rbac.entities",
//        "com.gossamercms.rbac.handlers",
//        "com.gossamercms.rbac.exceptions",
//        "com.gossamercms.rbac"
})
@EnableModuleModels(basePackage = "com.gossamercms.rbac.models")
//@EnableModuleDbServices(basePackage = "com.gossamercms.rbac.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.rbac.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.rbac.adapters.inbound")
@EnableModuleServices(basePackage = "com.gossamercms.rbac.services")
//@EnableModuleProviders(basePackage = "com.gossamercms.rbac.providers")
//@EnableModuleConverters(basePackage = "com.gossamercms.rbac.converters")
public class RbacAutoConfig {

    private static final long TTL = 60 * 60 * 1000;
    private static final PermissionCache permissionCache = new PermissionCache(TTL);

    @Bean
    public PermissionCache permissionCache() {
        return permissionCache;
    }

    @Bean
    public PermissionEvaluator permissionEvaluator(
            PermissionResolver resolver,
            PermissionCache cache
    ) {
        return new PermissionEvaluator(resolver, cache);
    }

    @Bean
    public PermissionEnforcementAspect permissionEnforcementAspect(
            PermissionResolver resolver,
            CurrentUserProvider currentUserProvider
    ) {
        return new PermissionEnforcementAspect(resolver, currentUserProvider);
    }


//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new PermissionInterceptor(evaluator));
//    }
    @Bean
    public WebMvcConfigurer rbacWebMvcConfigurer(PermissionEvaluator evaluator) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new PermissionInterceptor(evaluator));
            }
        };
    }

    @Bean
    public CurrentUserProvider currentUserProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ForbiddenException("No authenticated user");
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof JwtUser jwtUser) {
                return jwtUser.getUserId();
            }

            throw new ForbiddenException("Authenticated principal is not a JwtUser");
        };
    }


}