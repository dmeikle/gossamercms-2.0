package com.gossamercms.auth.filters;

import com.gossamercms.auth.dtos.requests.MutableHeaderRequest;
import com.gossamercms.security.jwt.JwtUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtUserHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof JwtUser jwtUser) {

            MutableHeaderRequest wrapped =
                    new MutableHeaderRequest(request);

            wrapped.addHeader(
                    "X-User-Id",
                    jwtUser.getUserId().toString()
            );

            chain.doFilter(wrapped, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
