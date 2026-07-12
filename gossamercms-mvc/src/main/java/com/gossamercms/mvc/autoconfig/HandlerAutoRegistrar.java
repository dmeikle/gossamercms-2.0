package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.HandlerScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class HandlerAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {

        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleHandlers.class.getName())
                .get("basePackage");

        Set<Class<?>> handlers = HandlerScanner.scan(basePackage);

        for (Class<?> handler : handlers) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(handler);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    handler.getSimpleName(),
                    def
            );
        }
    }
}