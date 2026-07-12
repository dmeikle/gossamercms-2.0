package com.gossamercms.security.loggers;

import org.springframework.security.web.FilterChainProxy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class SecurityFilterLogger {

    private final FilterChainProxy filterChainProxy;

    public SecurityFilterLogger(FilterChainProxy filterChainProxy) {
        this.filterChainProxy = filterChainProxy;
    }

    @PostConstruct
    public void logFilters() {

        filterChainProxy.getFilterChains().forEach(chain -> {

            System.out.println("--------------------------------");
            System.out.println("---------- Security Filter Chain ---------");

            System.out.println(chain);

            chain.getFilters().forEach(filter ->
                    System.out.println(
                            "  -> " + filter.getClass().getSimpleName()
                    ));
        });
    }
}
