package com.gossamercms.outbox.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import com.gossamercms.mvc.autoconfig.*;
        import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.outbox.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.outbox.data")
//@EnableModuleHandlers(basePackage = "com.gossamercms.outbox.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.outbox.adapters.inbound")
@EnableModuleServices(basePackage = "com.gossamercms.outbox.services")
@EnableModuleProviders(basePackage = "com.gossamercms.outbox.providers")
//@EnableModuleConverters(basePackage = "com.gossamercms.outbox.converters")
@ComponentScan(basePackages = "com.gossamercms.outbox.dispatcher") // Ensure all components are scanned
public class OutboxAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> OutboxAutoConfig LOADED");
        System.out.println("************************************************************");
    }

}