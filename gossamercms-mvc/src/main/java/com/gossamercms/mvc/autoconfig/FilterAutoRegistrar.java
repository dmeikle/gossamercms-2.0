package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.DbServiceScanner;
import com.gossamercms.mvc.scanning.FilterScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class FilterAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {

        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleFilters.class.getName())
                .get("basePackage");

        Set<Class<?>> filters = FilterScanner.scan(basePackage);

        for (Class<?> filter : filters) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(filter);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    filter.getSimpleName(),
                    def
            );
        }
    }
}