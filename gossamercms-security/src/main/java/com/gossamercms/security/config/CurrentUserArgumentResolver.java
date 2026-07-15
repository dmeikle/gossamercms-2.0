package com.gossamercms.security.config;

import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Resolves @CurrentUser annotated parameters to inject the authenticated JwtUser.
 *
 * Extracts the JwtUser from Spring Security's SecurityContextHolder and injects it
 * into method parameters marked with @CurrentUser.
 */
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        // No authentication → anonymous
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // AnonymousAuthenticationToken → anonymous
        if (auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof JwtUser user) {
            return user;
        }

        // Any other principal type → treat as anonymous
        return null;
    }
}


