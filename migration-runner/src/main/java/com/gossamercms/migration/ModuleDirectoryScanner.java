package com.gossamercms.migration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleDirectoryScanner {

    public static List<URL> findModuleClassUrls(Path root) {

        System.out.println("--------------------------------------------------");
        System.out.println("Scanning module root: " + root.toAbsolutePath());

        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new RuntimeException("Invalid module root: " + root.toAbsolutePath());
        }

        List<URL> urls = new ArrayList<>();

        try {
            Files.walk(root, 3)  // scan 3 levels deep
                    .filter(Files::isDirectory)
                    .forEach(dir -> {
                        Path classes = dir.resolve("target/classes");

                        if (Files.exists(classes)) {
                            try {
                                System.out.println("✔ Found compiled classes: " + classes);
                                urls.add(classes.toUri().toURL());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException("Error scanning modules", e);
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Final module class URLs:");
        urls.forEach(url -> System.out.println("  → " + url));

        return urls;
    }
}