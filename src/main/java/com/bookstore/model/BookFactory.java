package com.bookstore.model;

import com.bookstore.util.TextCodec;

/** Creates the correct Book subclass from a type code or from a line of books.txt. */
public final class BookFactory {

    private BookFactory() {
    }

    public static Book create(String type, String id, String title, String genre, String price,
                              String image, String authorId, String description, String format) {
        if (EBook.TYPE.equalsIgnoreCase(type)) {
            return new EBook(id, title, genre, price, image, authorId, description, format);
        }
        return new PrintedBook(id, title, genre, price, image, authorId, description, format);
    }

    public static Book create(String type, String id, String title, String genre, String price,
                              String image, Author author, String description, String format) {
        if (EBook.TYPE.equalsIgnoreCase(type)) {
            return new EBook(id, title, genre, price, image, author, description, format);
        }
        return new PrintedBook(id, title, genre, price, image, author, description, format);
    }

    /**
     * Reads one line of books.txt; returns null for a broken line.
     * Supports legacy 4/5-column lines and 9-column lines with backward compatibility.
     */
    public static Book fromFileLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] d = line.split(",", -1);
        if (d.length == 4 || d.length == 5) {
            // legacy format: id,title,genre,price[,image]
            return create(PrintedBook.TYPE, d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim(),
                    d.length == 5 ? d[4].trim() : "", "", "", "");
        }
        if (d.length >= 9) {
            String id = d[0].trim();
            String title = d[1].trim();
            String genre = d[2].trim();
            String price = d[3].trim();
            String image = d[4].trim();
            String authorRef = TextCodec.decode(d[5]);
            String description = TextCodec.decode(d[6]);
            String type = d[7].trim();
            String format = TextCodec.decode(d[8]);
            return create(type, id, title, genre, price, image, authorRef, description, format);
        }
        if (d.length >= 6) {
            String id = d[0].trim();
            String title = d[1].trim();
            String genre = d[2].trim();
            String price = d[3].trim();
            String image = d[4].trim();
            String authorRef = TextCodec.decode(d[5]);
            String description = d.length > 6 ? TextCodec.decode(d[6]) : "";
            String type = d.length > 7 ? d[7].trim() : PrintedBook.TYPE;
            return create(type, id, title, genre, price, image, authorRef, description, "");
        }
        return null;
    }
}
