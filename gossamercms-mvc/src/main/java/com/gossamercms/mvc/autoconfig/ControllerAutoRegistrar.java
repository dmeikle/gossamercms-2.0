package com.gossamercms.mvc.autoconfig;

import com.gossamercms.mvc.scanning.ControllerScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@AutoConfiguration
public class ControllerAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {

System.out.println("Registering controllers from package: " + metadata.getAnnotationAttributes(EnableModuleControllers.class.getName()).get("basePackage"));

        // The module using this auto-config must define this annotation
        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleControllers.class.getName())
                .get("basePackage");

        Set<Class<?>> controllers = ControllerScanner.scan(basePackage);

        for (Class<?> controller : controllers) {
            GenericBeanDefinition def = new GenericBeanDefinition();
            def.setBeanClass(controller);
            def.setAutowireMode(GenericBeanDefinition.AUTOWIRE_CONSTRUCTOR);

            registry.registerBeanDefinition(
                    controller.getSimpleName(),
                    def
            );
        }
    }
}