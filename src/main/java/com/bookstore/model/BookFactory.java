package com.bookstore.model;

import com.bookstore.util.TextCodec;

/** Creates the correct Book sub class from a type code or from a line of books.txt. */
public final class BookFactory {

    private BookFactory() {
    }

    public static Book create(String type, String id, String title, String genre, String price,
                              String image, String author, String description, String format) {
        if (EBook.TYPE.equalsIgnoreCase(type)) {
            return new EBook(id, title, genre, price, image, author, description, format);
        }
        return new PrintedBook(id, title, genre, price, image, author, description, format);
    }

    /** Reads one line of books.txt; returns null for a broken line. Old 4 / 5 field lines still work. */
    public static Book fromFileLine(String line) {
        String[] d = line.split(",", -1);
        if (d.length == 4 || d.length == 5) {
            // old format: id,title,genre,price[,image]
            return create(PrintedBook.TYPE, d[0], d[1], d[2], d[3],
                    d.length == 5 ? d[4] : "", "", "", "");
        }
        if (d.length == 9) {
            return create(d[7], d[0], d[1], d[2], d[3], d[4],
                    TextCodec.decode(d[5]), TextCodec.decode(d[6]), TextCodec.decode(d[8]));
        }
        return null;
    }
}