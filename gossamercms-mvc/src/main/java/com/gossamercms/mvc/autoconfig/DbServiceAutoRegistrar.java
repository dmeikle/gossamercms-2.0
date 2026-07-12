package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.DbServiceScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class DbServiceAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {

        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleDbServices.class.getName())
                .get("basePackage");

        Set<Class<?>> services = DbServiceScanner.scan(basePackage);

        for (Class<?> service : services) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(service);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    service.getSimpleName(),
                    def
            );
        }
    }
}