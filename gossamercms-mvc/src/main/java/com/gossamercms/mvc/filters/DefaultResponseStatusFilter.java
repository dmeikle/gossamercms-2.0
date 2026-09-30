package com.gossamercms.mvc.filters;

import com.gossamercms.mvc.interceptors.DefaultResponseStatusMetadataInterceptor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

public class DefaultResponseStatusFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        if (response.isCommitted() || response.getStatus() != HttpServletResponse.SC_OK) {
            return;
        }

        if (Boolean.TRUE.equals(request.getAttribute(
                DefaultResponseStatusMetadataInterceptor.SKIP_DEFAULT_RESPONSE_STATUS_ATTRIBUTE
        ))) {
            return;
        }

        HttpStatus status = defaultStatus(request.getMethod());
        if (status != null && status.value() != response.getStatus()) {
            response.setStatus(status.value());
        }
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
