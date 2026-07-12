package com.gossamercms.mvc.autoconfig;

import com.gossamercms.mvc.scanning.EventListenerScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class EventListenerAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {

System.out.println("Registering eventlisters from package: " + metadata.getAnnotationAttributes(EnableModuleEventListeners.class.getName()).get("basePackage"));

        // The module using this auto-config must define this annotation
        String[] basePackages = (String[]) metadata
                .getAnnotationAttributes(
                        EnableModuleEventListeners.class.getName()
                )
                .get("basePackage");

        for (String basePackage : basePackages) {

            Set<Class<?>> listeners =
                    EventListenerScanner.scan(basePackage);

            for (Class<?> listener : listeners) {

                GenericBeanDefinition def =
                        new GenericBeanDefinition();

                def.setBeanClass(listener);
                def.setAutowireMode(
                        GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR
                );

                registry.registerBeanDefinition(
                        listener.getCanonicalName(),
                        def
                );
            }
        }
    }
}