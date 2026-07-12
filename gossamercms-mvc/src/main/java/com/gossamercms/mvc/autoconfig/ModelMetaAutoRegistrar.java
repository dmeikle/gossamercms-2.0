package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.ModelMetaScanner;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.mvc.models.ModelMetaRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;
import java.util.Set;

@AutoConfiguration
public class ModelMetaAutoRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata metadata,
            BeanDefinitionRegistry registry
    ) {
        String basePackage = (String) metadata
                .getAnnotationAttributes(EnableModuleModels.class.getName())
                .get("basePackage");

        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(ModelMetaLoader.class);
        def.getConstructorArgumentValues().addGenericArgumentValue(basePackage);

        registry.registerBeanDefinition("modelMetaLoader_" + basePackage, def);
    }
}