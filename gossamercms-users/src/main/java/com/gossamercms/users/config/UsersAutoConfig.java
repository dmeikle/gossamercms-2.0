package com.gossamercms.users.config;

import com.gossamercms.mvc.autoconfig.*;
import com.gossamercms.users.converters.UserConverter;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.users.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.users.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.users.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.users.adapters.inbound")
@EnableModuleConverters(basePackage = "com.gossamercms.users.converters")
@ComponentScan(basePackages = "com.gossamercms.users.listeners") // Ensure all components are scanned
public class UsersAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> UsersAutoConfig LOADED");
        System.out.println("************************************************************");
    }



}