package com.gossamercms.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MigrationWriter {

    public static void writeMigration(Path resourcesDir, String table, String sql) throws IOException {
        Path migrationDir = resourcesDir.resolve("db/migration/sql");
        System.out.println("************************migration path " + migrationDir);
        Files.createDirectories(migrationDir);

        int version = nextVersion(migrationDir);

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String filename = "V" + version + "__" + timestamp + "_create_" + table + "_table.sql";

        Path file = migrationDir.resolve(filename);

        Files.writeString(file, sql);

        System.out.println("Generated migration: " + file.toAbsolutePath());
    }

    private static int nextVersion(Path migrationDir) throws IOException {
        if (!Files.exists(migrationDir)) return 1;

        return Files.list(migrationDir)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(name -> name.startsWith("V"))
                .map(name -> name.substring(1, name.indexOf("__")))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0) + 1;
    }
}