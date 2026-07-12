package com.gossamercms.migration;

import java.nio.file.*;

public class ModuleLocator {

    public static Path findResourcesFor(Class<?> modelClass, Path inputDir) {
        try {
            // Extract module name from package
            // com.gossamercms.users.domain.User → "users"
            String[] parts = modelClass.getPackageName().split("\\.");
            String moduleName = parts[2];
            String moduleFolder = "gossamercms-" + moduleName;

            // Build module path
            return inputDir
                    .resolve(moduleFolder)
                    .resolve("src/main/resources");

        } catch (Exception e) {
            throw new RuntimeException("Failed to locate module resources for " + modelClass, e);
        }
    }
}