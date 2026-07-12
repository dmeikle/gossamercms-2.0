package com.gossamercms.migration;

import com.gossamercms.mvc.models.BaseModel;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ModelScanner {

    public static List<Class<? extends BaseModel>> findAllModels(Path inputPath) {

        List<URL> moduleUrls = ModuleDirectoryScanner.findModuleClassUrls(inputPath);

        System.out.println("Building Reflections scanner with URLs:");
        moduleUrls.forEach(url -> System.out.println("  → " + url));

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(moduleUrls)
                        .setScanners(new SubTypesScanner(false))
        );

        System.out.println("Scanning for BaseModel subclasses...");

        Set<Class<? extends BaseModel>> found = reflections.getSubTypesOf(BaseModel.class);

        System.out.println("Raw scan results:");
        found.forEach(c -> System.out.println("  → " + c.getName()));

        List<Class<? extends BaseModel>> models = found.stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .toList();

        System.out.println("Filtered (non-abstract) models:");
        models.forEach(c -> System.out.println("  ✔ " + c.getName()));

        return models;
    }
}