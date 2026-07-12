package com.gossamercms.mvc.scanning;

import com.gossamercms.mvc.annotations.ModuleConverter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Set;
import java.util.stream.Collectors;

public class ConverterScanner {

    public static Set<Class<?>> scan(String basePackage) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(ModuleConverter.class));

        return scanner.findCandidateComponents(basePackage)
                .stream()
                .map(bean -> {
                    try {
                        return Class.forName(bean.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }
}