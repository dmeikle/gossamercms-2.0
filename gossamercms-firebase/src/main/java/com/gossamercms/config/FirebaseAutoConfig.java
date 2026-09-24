package com.gossamercms.config;


import com.gossamercms.mvc.autoconfig.EnableModuleProviders;
import com.gossamercms.mvc.autoconfig.EnableModuleServices;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableConfigurationProperties(FirebaseProperties.class)
//@EnableModuleModels(basePackage = "com.gossamercms.firebase.models")
//@EnableModuleDbServices(basePackage = "com.gossamercms.firebase.data")
//@EnableModuleHandlers(basePackage = "com.gossamercms.firebase.handlers")
@EnableModuleServices(basePackage = "com.gossamercms.firebase.services")
@EnableModuleProviders(basePackage = "com.gossamercms.firebase.providers")
//@EnableModuleConverters(basePackage = "com.gossamercms.firebase.converters")
//@EnableModuleEventListeners(basePackage = "com.gossamercms.firebase.listeners")
@ComponentScan(basePackages = "com.gossamercms.firebase.listeners") // Ensure all components are scanned
public class FirebaseAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> FirebaseAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}