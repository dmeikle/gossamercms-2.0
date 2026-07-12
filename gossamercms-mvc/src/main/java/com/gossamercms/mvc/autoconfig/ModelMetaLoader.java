package com.gossamercms.mvc.autoconfig;


import com.gossamercms.mvc.scanning.ModelMetaScanner;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.mvc.models.ModelMetaRegistry;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Field;
import java.util.Set;

public class ModelMetaLoader implements InitializingBean {

    private final String basePackage;
    private final ModelMetaRegistry registry;

    public ModelMetaLoader(String basePackage, ModelMetaRegistry registry) {
        this.basePackage = basePackage;
        this.registry = registry;
    }

    @Override
    public void afterPropertiesSet() {
        Set<Class<?>> models = ModelMetaScanner.scan(basePackage);

        for (Class<?> model : models) {
            try {
                Field field = model.getDeclaredField("META");
                field.setAccessible(true);
                ModelMeta meta = (ModelMeta) field.get(null);
                registry.register(meta);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load META from " + model.getName(), e);
            }
        }
    }
}