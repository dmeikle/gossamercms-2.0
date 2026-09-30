package com.gossamercms.mvc.handlers;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Locale;

@RestControllerAdvice
public class DefaultResponseStatusAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> containingClass = returnType.getContainingClass();
        return AnnotatedElementUtils.findMergedAnnotation(returnType.getMethod(), ResponseStatus.class) == null
                && AnnotatedElementUtils.findMergedAnnotation(containingClass, ResponseStatus.class) == null
                && !HttpEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        HttpStatus status = defaultStatus(request.getMethod() == null ? null : request.getMethod().name());
        if (status != null) {
            response.setStatusCode(status);
        }
        return body;
    }

    private HttpStatus defaultStatus(String method) {
        if (method == null || method.isBlank()) {
            return null;
        }

        return switch (method.toUpperCase(Locale.ROOT)) {
            case "GET", "PUT", "PATCH" -> HttpStatus.OK;
            case "POST" -> HttpStatus.CREATED;
            case "DELETE" -> HttpStatus.NO_CONTENT;
            default -> null;
        };
    }
}
