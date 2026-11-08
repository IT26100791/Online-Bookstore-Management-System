package com.bookstore.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Decides which folder holds the data files (users.txt, authors.txt, author-images, ...).
 * Nothing in here is specific to one PC, so every team member gets the right folder.
 *
 * Order of choice:
 *  1. -Dbookstore.data=<folder>   (optional manual override, e.g. in the Smart Tomcat VM options)
 *  2. the project root             (the folder that contains pom.xml, found from the compiled classes)
 *  3. <user home>/bookstore-data   (fallback when the project root cannot be found)
 */
public final class DataPaths {
    private static final String DATA_DIR = findDataDir();

    private DataPaths() {}

    public static String dataDir() {
        return DATA_DIR;
    }

    private static String findDataDir() {
        // 1. manual override
        String override = System.getProperty("bookstore.data");
        if (override != null && !override.trim().isEmpty()) {
            return override.trim();
        }

        // 2. walk up from the compiled classes (target/classes) until pom.xml is found
        try {
            Path p = Paths.get(DataPaths.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            for (int i = 0; i < 8 && p != null; i++) {
                if (new File(p.toFile(), "pom.xml").isFile()) {
                    return p.toString();
                }
                p = p.getParent();
            }
        } catch (Exception e) {
            // fall through to the fallback below
        }

        // 3. fallback: a folder in the user's own home directory
        return System.getProperty("user.home") + File.separator + "bookstore-data";
    }
}
