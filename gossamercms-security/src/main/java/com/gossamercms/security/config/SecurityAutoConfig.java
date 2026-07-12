package com.gossamercms.security.config;

import com.gossamercms.mvc.autoconfig.EnableModuleControllers;
import com.gossamercms.mvc.autoconfig.EnableModuleHandlers;
import com.gossamercms.mvc.autoconfig.EnableModuleServices;
import com.gossamercms.security.filters.JwtAuthenticationFilter;
import com.gossamercms.security.jwt.JwtProperties;
import com.gossamercms.security.services.JwtClaimsMapper;
import com.gossamercms.security.services.JwtService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
@EnableModuleServices(basePackage = "com.gossamercms.security.services")
@EnableModuleHandlers(basePackage = "com.gossamercms.security.application")
@EnableModuleControllers(basePackage = "com.gossamercms.security.adapters.inbound")
public class SecurityAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> SecurityAutoConfig LOADED");
        System.out.println("************************************************************");
    }

    @Bean
    public JwtService jwtService(JwtProperties props) {
        return new JwtService(props.getSecret(), props.getTtlSeconds());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            JwtClaimsMapper jwtClaimsMapper
    ) {
        return new JwtAuthenticationFilter(
                jwtService,
                jwtClaimsMapper
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register","/*/*", "/admin/auth/*","/admin/auth/register").permitAll()
                      //  .anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(new CurrentUserArgumentResolver());
            }
        };
    }

    @Bean
    public JwtClaimsMapper jwtClaimsMapper() {
        return new JwtClaimsMapper();
    }

}
