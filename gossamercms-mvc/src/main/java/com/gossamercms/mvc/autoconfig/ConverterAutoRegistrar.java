package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.ConverterScanner;
import com.gossamercms.mvc.scanning.HandlerScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class ConverterAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {


        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleConverters.class.getName())
                .get("basePackage");

        Set<Class<?>> converters = ConverterScanner.scan(basePackage);

        for (Class<?> converter : converters) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(converter);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    converter.getSimpleName(),
                    def
            );
        }
    }
}