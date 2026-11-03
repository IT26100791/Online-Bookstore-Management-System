package com.bookstore.model;

public class Book {
    private final String id;
    private final String title;
    private final String genre;
    private final String price;
    private final String image;   // saved cover file name, "" when the book has no cover

    // original constructor kept, so any existing code that calls it still works
    public Book(String id, String title, String genre, String price) {
        this(id, title, genre, price, "");
    }

    public Book(String id, String title, String genre, String price, String image) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.price = price;
        this.image = image == null ? "" : image;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPrice() { return price; }
    public String getImage() { return image; }

    public boolean hasImage() { return !image.isEmpty(); }

    // books without a cover are written in the old 4-field format, so books.txt stays compatible
    public String toFileLine() {
        String line = id + "," + title + "," + genre + "," + price;
        return image.isEmpty() ? line : line + "," + image;
    }
}
