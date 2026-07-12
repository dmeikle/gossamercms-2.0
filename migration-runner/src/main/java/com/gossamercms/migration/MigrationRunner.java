package com.gossamercms.migration;

import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MigrationRunner {

    private static final Path PROJECT_ROOT = Paths.get("")
            .toAbsolutePath()
            .normalize()
            .getParent()   // migration-runner/
            .getParent()   // gossamercms-toolset/
            .getParent();   // gossamercms-java/

    private static final Path INPUT_DIR = PROJECT_ROOT.resolve("gossamercms-modules");
    private static final Path OUTPUT_DIR = PROJECT_ROOT.resolve("gossamercms-modules");


    public static void main(String[] args) throws Exception {
        System.out.println("Running migration generator...");
        System.out.println("MigrationRunner location: " +
                Paths.get("").toAbsolutePath());

        System.out.println("FROM " + INPUT_DIR);
        System.out.println("TO   " + OUTPUT_DIR);


        List<Class<? extends BaseModel>> models = ModelScanner.findAllModels(INPUT_DIR);
        System.out.println("BaseModel loaded from: " +
                BaseModel.class.getProtectionDomain().getCodeSource().getLocation());

        Class<?> userClass = Class.forName("com.gossamercms.users.domain.User");
        System.out.println("User loaded from: " +
                userClass.getProtectionDomain().getCodeSource().getLocation());

        System.out.println("Does User implement BaseModel? " +
                BaseModel.class.isAssignableFrom(userClass));
        if (models.isEmpty()) {
            System.out.println("⚠ No models found! Check scanner output above.");
        }

        for (Class<? extends BaseModel> model : models) {
            ModelMeta meta = BaseModel.metaOf(model);

            Path resources = ModuleLocator.findResourcesFor(model, INPUT_DIR);
            Path outputPath = ModuleLocator.findResourcesFor(model, OUTPUT_DIR) ;

            System.out.println("→ Writing migration to: " + resources);

            String sql = MigrationGenerator.generateCreateTableSql(meta);
System.out.println("******** writing to " + outputPath);
            MigrationWriter.writeMigration(outputPath, meta.table(), sql);
        }

        System.out.println("Done.");
    }
}