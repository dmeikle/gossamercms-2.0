package com.gossamercms.mvc.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class DefaultResponseStatusMetadataInterceptor implements HandlerInterceptor {

    public static final String SKIP_DEFAULT_RESPONSE_STATUS_ATTRIBUTE =
            DefaultResponseStatusMetadataInterceptor.class.getName() + ".skip";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean skipDefaultStatus =
                AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), ResponseStatus.class) != null
                        || AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), ResponseStatus.class) != null
                        || HttpEntity.class.isAssignableFrom(handlerMethod.getMethod().getReturnType());

        request.setAttribute(SKIP_DEFAULT_RESPONSE_STATUS_ATTRIBUTE, skipDefaultStatus);
        return true;
    }
}
