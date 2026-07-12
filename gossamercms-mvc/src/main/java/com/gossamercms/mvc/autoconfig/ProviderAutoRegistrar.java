package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.annotations.ModuleProvider;
import com.gossamercms.mvc.scanning.ConverterScanner;
import com.gossamercms.mvc.scanning.ProviderScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

@AutoConfiguration
public class ProviderAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleProviders.class.getName())
                .get("basePackage");

        Set<Class<?>> providers = ProviderScanner.scan(basePackage);

        for (Class<?> provider : providers) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(provider);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    provider.getSimpleName(),
                    def
            );
        }
    }
}