package com.gossamercms.auth.config;


import com.gossamercms.mvc.autoconfig.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.auth.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.auth.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.auth.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.auth.controllers")
@EnableModuleEventListeners(basePackage = "com.gossamercms.auth.listeners")
@ComponentScan(basePackages = {
        "com.gossamercms.auth.factories"
})
public class AuthAutoConfig {


    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> AuthAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}