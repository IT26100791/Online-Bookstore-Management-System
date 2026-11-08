package com.bookstore.model;

import java.util.Arrays;
import java.util.List;

/** A digital book, available to download straight after buying. */
public class EBook extends Book {
    public static final String TYPE = "EBOOK";
    public static final List<String> FILE_FORMATS = Arrays.asList("PDF", "EPUB", "MOBI");

    private final String fileFormat;

    public EBook(String id, String title, String genre, String price,
                 String image, String author, String description, String fileFormat) {
        super(id, title, genre, price, image, author, description);
        this.fileFormat = (fileFormat == null || fileFormat.isEmpty()) ? FILE_FORMATS.get(0) : fileFormat;
    }

    @Override
    public String getType() { return TYPE; }

    @Override
    public String getTypeLabel() { return "E-book"; }

    @Override
    public String getFormatLabel() { return "File format"; }

    @Override
    public String getFormatValue() { return fileFormat; }

    @Override
    public String getDeliveryNote() { return "Instant download after purchase - no delivery needed."; }
}