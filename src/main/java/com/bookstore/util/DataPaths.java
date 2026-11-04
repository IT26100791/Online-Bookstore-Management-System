package com.bookstore.util;

public final class DataPaths {
    private DataPaths() {}

    public static String dataDir() {
        return System.getProperty("bookstore.data",
                "C:/Users/Pathum Anjana/IdeaProjects/Bookstore Management System v4");
    }
}