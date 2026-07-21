package com.gossamercms.utilities.config;

import com.gossamercms.mvc.autoconfig.EnableModuleConverters;
import com.gossamercms.mvc.autoconfig.EnableModuleDbServices;
import com.gossamercms.mvc.autoconfig.EnableModuleHandlers;
import com.gossamercms.mvc.autoconfig.EnableModuleModels;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@AutoConfiguration
@EnableModuleModels(basePackage = "com.gossamercms.utilities.languages.models")
@EnableModuleDbServices(basePackage = "com.gossamercms.utilities.languages.data")
@EnableModuleHandlers(basePackage = "com.gossamercms.utilities.languages.handlers")
//@EnableModuleControllers(basePackage = "com.gossamercms.utilities.languages.adapters.inbound")
@EnableModuleConverters(basePackage = "com.gossamercms.utilities.languages.converters")
@ComponentScan(basePackages = "com.gossamercms.utilities.languages.listeners") // Ensure all components are scanned
public class UtilitiesAutoConfig {

    @PostConstruct
    public void init() {
        System.out.println("************************************************************");
        System.out.println(">>> UtilitiesAutoConfig LOADED");
        System.out.println("************************************************************");
    }



}