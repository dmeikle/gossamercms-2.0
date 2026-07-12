package com.gossamercms.media.config;


import com.gossamercms.mvc.autoconfig.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.media.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.media.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.media.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.media.controllers")
@EnableModuleServices(basePackage = "com.gossamercms.media.services")
@EnableModuleProviders(basePackage = "com.gossamercms.media.providers")
//@EnableModuleConverters(basePackage = "com.gossamercms.media.converters")
//@ComponentScan(basePackages = "com.gossamercms.media.listeners") // Ensure all components are scanned
public class MediaAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> MediaAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}
