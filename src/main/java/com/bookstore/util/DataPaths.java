package com.bookstore.util;

/** One place that decides where all the .txt data files live. */
public final class DataPaths {

    private DataPaths() {
    }

    /** -Dbookstore.data=<path> if given, otherwise <working dir>/data */
    public static String dataDir() {
        return System.getProperty("bookstore.data",
                System.getProperty("user.dir") + "/data");
    }
}
