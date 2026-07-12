package com.gossamercms.mvc.autoconfig;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConverterAutoRegistrar.class)
public @interface EnableModuleConverters {
    String basePackage();
}
