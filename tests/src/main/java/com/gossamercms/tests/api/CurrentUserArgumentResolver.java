package com.gossamercms.tests.api;

import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String CURRENT_USER_REQUEST_ATTRIBUTE = CurrentUserArgumentResolver.class.getName() + ".currentUser";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && JwtUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        return webRequest.getAttribute(CURRENT_USER_REQUEST_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }
}
