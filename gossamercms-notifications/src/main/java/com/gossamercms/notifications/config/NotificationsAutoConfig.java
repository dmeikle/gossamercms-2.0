package com.gossamercms.notifications.config;

import com.gossamercms.mvc.autoconfig.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;



@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.notifications.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.notifications.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.notifications.handlers")
@EnableModuleServices(basePackage = "com.gossamercms.notifications.services")
@EnableModuleProviders(basePackage = "com.gossamercms.notifications.providers")
@EnableModuleConverters(basePackage = "com.gossamercms.notifications.converters")
@EnableModuleEventListeners(basePackage = "com.gossamercms.notifications.listeners")
@ComponentScan(basePackages = "com.gossamercms.notifications.listeners") // Ensure all components are scanned
public class NotificationsAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> NotificationsAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}