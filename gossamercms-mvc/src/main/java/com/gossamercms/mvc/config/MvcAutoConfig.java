package com.gossamercms.mvc.config;

import com.gossamercms.mvc.autoconfig.EnableModuleControllers;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.models.ModelMetaRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableModuleControllers(basePackage = "com.gossamercms.mvc.controllers")
public class MvcAutoConfig {


    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> MvcAutoConfig LOADED");
        System.out.println("************************************************************");
    }


    @Bean
    public DataSourceManager dataSourceManager() {
        return new DataSourceManager();
    }

    @Bean
    public ModelMetaRegistry modelMetaRegistry() {
        return new ModelMetaRegistry();
    }
}
