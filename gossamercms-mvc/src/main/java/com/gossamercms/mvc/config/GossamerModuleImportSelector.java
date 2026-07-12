package com.gossamercms.mvc.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class GossamerModuleImportSelector implements ImportSelector {

    private static final String BASE_PACKAGE = "com.gossamercms";
    private static final String CONFIG_PATTERN = "/**/config/*AutoConfig.class";
    public GossamerModuleImportSelector() {
        System.out.println(">>> GossamerModuleImportSelector CONSTRUCTED");
    }
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {

        try {
            List<String> configs = new ArrayList<>();
System.out.println("Discovering module auto-configs...");
            // 1. Always load MVC first
            configs.add("com.gossamercms.mvc.config.MvcAutoConfig");

            // 2. Scan for all other AutoConfig classes
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            String path = "classpath*:" +
                    ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE) +
                    CONFIG_PATTERN;

            Resource[] resources = resolver.getResources(path);

            for (Resource resource : resources) {
                String className = toClassName(resource);

                if (className != null &&
                        !className.equals("com.gossamercms.mvc.config.MvcAutoConfig")) {
System.out.println("Found auto-config: " + className);
                    configs.add(className);
                }
            }

            return configs.toArray(String[]::new);

        } catch (Exception e) {
            throw new RuntimeException("Failed to discover module auto-configs", e);
        }
    }

    private String toClassName(Resource resource) throws Exception {
        String uri = resource.getURI().toString();

        int idx = uri.indexOf("/classes/");
        if (idx == -1) return null;

        String classPath = uri.substring(idx + "/classes/".length());

        if (!classPath.endsWith(".class")) return null;

        return classPath
                .replace('/', '.')
                .replace('\\', '.')
                .replace(".class", "");
    }
}