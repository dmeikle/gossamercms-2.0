package com.gossamercms.rbac.config;

import com.gossamercms.rbac.auth.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//@Configuration
public class RbacConfig implements WebMvcConfigurer {

//    private final PermissionEvaluator evaluator;
//
//
//    public RbacConfig(PermissionEvaluator evaluator) {
//        this.evaluator = evaluator;
//    }
//
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new PermissionInterceptor(evaluator));
//    }


//    @Bean
//    public PermissionEnforcementAspect permissionEnforcementAspect(
//            PermissionResolver resolver,
//            CurrentUserProvider currentUserProvider
//    ) {
//        return new PermissionEnforcementAspect(resolver, currentUserProvider);
//    }
}
